package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

/**
 * 買い物かごコントローラー
 * @author sakagami ryosuke
 */
@Controller
public class ClientBasketController {

	@Autowired
	HttpSession session;

	@PostMapping("/client/basket/add")
	public String addBasket(Integer itemId) {
		// 買い物かごリストの宣言
		List<BasketItem> basket;

		// セッションスコープに買い物かご情報があるかを確認
		if (session.getAttribute("basket") == null) {
			// なければ、買い物かご情報を生成
			basket = new ArrayList<BasketItem>();
			// 買い物かごにアイテムを追加
			BasketItem bI = new BasketItem(itemId);
			basket.add(bI);
		} else {
			// 買い物かごに追加対象の商品があるかを確認
			// 買い物かごの取り出し
			basket = (List<BasketItem>) session.getAttribute("basket");

			// 追加する商品がすでにカートに存在しているか確認
			for (int i = 0; i < basket.size(); i++) {
				// カート内容の取り出し
				BasketItem bI = basket.get(i);

				if (bI.getItemId() == itemId) {
					bI.setQuantity(bI.getQuantity() + 1);
				}
				// 存在しない場合
				else {
					// 買い物かごにアイテムを追加
					basket.add(bI);
				}
			}
		}
		// セッションに買い物かごデータを代入
		session.setAttribute("basket", basket);
		// カート一覧画面に遷移
		return "redilect:/client/basket/list";
	}

}
