package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
	
	/**
	 * 一覧データ取得、一覧表示　処理
	 *
	 * @param model Viewとの値受渡し
	 * @param pageable ページ制御用
	 * @return "admin/category/list" 一覧画面 表示
	 */
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET})
	public String showItemList(Model model, Pageable pageable) {

		// 商品情報の登録数の取得と新規追加可否チェック
		Long itemsCount = itemRepository.count();
		Boolean registrable = true;
		if (itemsCount == Constant.ITEMS_MAX_COUNT) {
			//商品情報の登録数が最大値の場合、新規追加不可
			registrable = false;
		}
		Page<Item> itemsPage = itemRepository.findByDeleteFlagOrderByInsertDateDescPage(Constant.NOT_DELETED, pageable);

		// エンティティ内の検索結果をJavaBeansにコピー
		List<Item> itemList = itemsPage.getContent();

		// 商品情報をViewへ渡す
		model.addAttribute("registrable", registrable);
		model.addAttribute("pages", itemsPage);
		model.addAttribute("items", itemList);

		return "/client/item/list/{sortType}";
	}
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