package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;

/**
 * 買い物かごコントローラー
 * @author sakagami ryosuke
 */
@Controller
public class ClientBasketController {

	@Autowired
	HttpSession session;
	@Autowired
	ItemRepository iR;
	List<BasketBean> basket;

	/**
	 * 買い物かご一覧表示
	 * @author sakagami ryosuke
	 * @return
	 */
	@RequestMapping(path = "/client/basket/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String basketList(Model model) {
		// セッションスコープに買い物かご情報があるかを確認
		if (session.getAttribute("basket") == null) {
			// なければ、買い物かご情報を生成
			basket = new ArrayList<BasketBean>();
			// セッションに買い物かごデータを代入
			session.setAttribute("basket", basket);
		} else {
			//sessionから買い物かごを取得
			basket = (List<BasketBean>) session.getAttribute("basket");

			// 在庫切れ商品リスト作成
			List<String> stockLackItems = new ArrayList<String>();
			// 在庫ゼロ商品リストの作成
			List<String> stockZeroItems = new ArrayList<String>();
			// 買い物かごが存在する場合
			if (!basket.isEmpty()) {
				// 在庫数を最新の状態に更新
				for (int i = 0; i < basket.size(); i++) {
					// カート内アイテムの取り出し
					BasketBean bB = basket.get(i);
					// 在庫状況を更新
					bB.setStock(iR.getReferenceById(bB.getId()).getStock());
					// リストに保存
					basket.set(i, bB);

					// 注文数が在庫数を比較
					if (bB.getOrderNum() > bB.getStock()) {
						// 在庫が0の場合
						if (bB.getStock() == 0) {
							// 在庫切れ商品名をリストに追加
							stockZeroItems.add(bB.getName());
							// 在庫切れの商品は、買い物かごか情報ら削除
							basket.remove(i);
							// 添え字を調整
							i--;
						}
						// 在庫が不足している場合
						else {
							// 在庫不足商品名をリストに追加
							stockLackItems.add(bB.getName());
							// 在庫数にあわせて、買い物かご情報を更新（注文数、在庫数）
							bB.setOrderNum(bB.getStock());
							// リストに保存
							basket.set(i, bB);
						}
					}

					// 在庫0の場合
				}
				// セッションに買い物かごデータを代入
				session.setAttribute("basket", basket);
			}
			// 注文警告メッセージをリクエストスコープに保存
			model.addAttribute("stockZeroItems", stockZeroItems);
			model.addAttribute("stockLackItems", stockLackItems);
		}

		return "/client/basket/list";
	}

	/**
	 * 買い物かご追加
	 * @author sakagami ryosuke
	 * 
	 * @param itemId
	 * @return
	 */
	@PostMapping("/client/basket/add")
	public String addBasket(Integer id) {
		// アイテム情報を取得
		Item item = iR.getReferenceById(id);
		// セッションスコープに買い物かご情報があるかを確認
		if (session.getAttribute("basket") == null) {
			// なければ、買い物かご情報を生成
			basket = new ArrayList<BasketBean>();
		} else {
			// 買い物かごの取り出し
			basket = (List<BasketBean>) session.getAttribute("basket");
		}

		// 追加する商品がすでにカートに存在しているか確認
		if (basket.size() > 0) {
			for (int i = 0; i <= basket.size(); i++) {
				// カート内アイテムの取り出し
				BasketBean bB = basket.get(i);

				// 存在確認
				if (bB.getId() == id) {
					// 在庫数を増やす
					bB.setOrderNum(bB.getOrderNum() + 1);
					// 更新済みのデータをセット
					basket.set(i, bB);
					break;
				} else if (i == basket.size() - 1) {
					// 買い物かごにアイテムを追加
					BasketBean basketBean = new BasketBean(item.getId(), item.getName(), item.getStock());
					basket.add(basketBean);
					break;
				}
			}
		}else {
			// 買い物かごにアイテムを追加
			BasketBean basketBean = new BasketBean(item.getId(), item.getName(), item.getStock());
			basket.add(basketBean);
		}

		// セッションに買い物かごデータを代入
		session.setAttribute("basket", basket);
		// カート一覧画面に遷移
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごの商品を減らす
	 * @author sakagami ryosuke
	 * @param id
	 * @return
	 */
	@PostMapping("/client/basket/delete/{id}")
	public String deleteBasket(@PathVariable Integer id) {
		// 買い物かごの取り出し
		basket = (List<BasketBean>) session.getAttribute("basket");

		// 商品の照合
		for (int i = 0; i < basket.size(); i++) {
			// 買い物かご内アイテムの取り出し
			BasketBean bB = basket.get(i);

			// 商品の照合
			if (bB.getId() == id) {
				// 注文数が2以上の場合数を減らす
				if (bB.getOrderNum() >= 2) {
					bB.setOrderNum(bB.getOrderNum() - 1);
				} else {
					// 買い物かごリストから商品を削除
					basket.remove(i);
				}
				break;
			}
		}

		// セッションに買い物かごデータを代入
		session.setAttribute("basket", basket);

		// カート一覧画面に遷移
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごを空にする
	 * @author sakagami ryosuke
	 * @return
	 */
	@PostMapping("/basket/deleteAll")
	public String deleteAllBasket() {
		// 買い物かごの取り出し
		basket.clear();
		// セッションに買い物かごデータを代入
		session.removeAttribute("basket");
		// カート一覧画面に遷移
		return "redirect:/client/basket/list";
	}
}
