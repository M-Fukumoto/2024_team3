package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	
	// test
	@RequestMapping("/client/basket/test")
	public String basketTest(Model model) {
		
		List<BasketBean>basket = new ArrayList<BasketBean>();
		// 買い物かごにアイテムを追加
		BasketBean bB = new BasketBean(1,"りんご",5);
		basket.add(bB);
		session.setAttribute("basket", basket);
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
		// 買い物かごリストの宣言
		List<BasketBean> basket;

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
}
