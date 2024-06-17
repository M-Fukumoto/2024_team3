package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	
	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/" , method = RequestMethod.GET)
	public String index(Model model, Pageable pageable) {

			// 商品情報を全件検索(売れ筋順)
			List<Item> itemsPage = itemRepository.findAllByOrderByCountAllDesc();
			if (itemsPage == null) {
			// 商品情報を全件検索(新着順)
			itemsPage = itemRepository.findAllByOrderByInsertDate();
			}

			// 商品情報をViewへ渡す
			model.addAttribute("items", itemsPage);

			
			return "/index";
		}
	
	
//	・表示内容の取得については、表示順変更(新着順)、表示順変更
//	(売れ筋順)、カテゴリ別検索の機能説明に記載
//	・検索条件、表示順に合わせた一覧表示
//	・検索条件、表示順の指定がない場合は、すべての商品を「新着
//	順」で取得し表示
	
	
	
	/**
 * 商品一覧表示処理
 *
 * @author ko teiketsu
 * @param id  商品ID
 * @param model  Viewとの値受渡し
 * @return "client/item/detail" 詳細画面 表示
 */
	

	
	
		/**
	 * 商品情報詳細表示処理
	 *
	 * @author ko teiketsu
	 * @param id  商品ID
	 * @param model  Viewとの値受渡し
	 * @return "client/item/detail" 詳細画面 表示
	 */
	@RequestMapping(path = "/client/item/detail/{id}", method = RequestMethod.GET)
	public String showItem(@PathVariable int id, Model model) {

		// 対象の商品情報を取得
		Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);

	if (item == null) {
			// 対象が無い場合、エラー
	return "redirect:/syserror";
		}

		//Itemエンティティの各フィールドの値をItemBeanにコピー
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);

		// 商品情報をViewへ渡す
		model.addAttribute("item", itemBean);
		
		
	return "client/item/detail";
	
	}
}