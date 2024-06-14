package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	
	// test
	@RequestMapping("/client/basket/test")
	public String basketTest(Model model) {
		basket = new ArrayList<BasketBean>();
		// 買い物かごにアイテムを追加
		BasketBean bB = new BasketBean(1,"りんご",50);
		bB.setOrderNum(5);
		basket.add(bB);
		session.setAttribute("basket", basket);
		return "/client/basket/list";
	}
	
	/**
	 * 買い物かご一覧表示
	 * @author sakagami ryosuke
	 * @return
	 */
	@GetMapping("/client/basket/list")
	public String basketList() {
		//sessionから買い物かごを取得
		basket = (List<BasketBean>) session.getAttribute("basket");
		
		// 買い物かごが存在する場合
		if(Objects.nonNull(basket)) {
			// 在庫数を最新の状態に更新
			for (int i = 0; i < basket.size(); i++) {
				// カート内アイテムの取り出し
				BasketBean bB = basket.get(i);
				// 在庫状況を更新
				bB.setStock(iR.getReferenceById(bB.getId()).getStock());
				// セッションに買い物かごデータを代入
				session.setAttribute("basket", basket);
			}
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
	public String addBasket(Integer itemId) {
		// セッションスコープに買い物かご情報があるかを確認
		if (session.getAttribute("basket") == null) {
			// なければ、買い物かご情報を生成
			basket = new ArrayList<BasketBean>();
			// アイテム情報を取得
			 Item item = iR.getReferenceById(itemId);
			// 買い物かごにアイテムを追加
			BasketBean bB = new BasketBean(item.getId(),item.getName(),item.getStock());
			basket.add(bB);
		} else {
			// 買い物かごに追加対象の商品があるかを確認
			// 買い物かごの取り出し
			basket = (List<BasketBean>) session.getAttribute("basket");

			// 追加する商品がすでにカートに存在しているか確認
			for (int i = 0; i < basket.size(); i++) {
				// カート内アイテムの取り出し
				BasketBean bB = basket.get(i);

				if (bB.getId() == itemId) {
					bB.setOrderNum(bB.getOrderNum() + 1);
				}
				// 存在しない場合
				else {
					// 買い物かごにアイテムを追加
					basket.add(bB);
				}
			}
		}
		// セッションに買い物かごデータを代入
		session.setAttribute("basket", basket);
		// カート一覧画面に遷移
		return "redilect:/client/basket/list";
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
				if(bB.getOrderNum() >= 2) {
				bB.setOrderNum(bB.getOrderNum() - 1);
				}else {
					// 買い物かごリストから商品を削除
					basket.remove(i);
				}
				break;
			}
		}
		// 買い物かごの件数が0の時
		if(basket.size()==0) {
			// セッションから買い物かごを削除
			session.removeAttribute("basket");
		}else {
			// セッションに買い物かごデータを代入
			session.setAttribute("basket", basket);
		}
		
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
		// セッションの買い物かご情報を削除
		session.removeAttribute("basket");
		// カート一覧画面に遷移
		return "redirect:/client/basket/list";
	}
}
