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
	 * 並び順 
	 */
	private String sortType;

	/**
	 * トップ画面 表示処理
	 *
	 * @author ko teiketsu ryosuke sakagami
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String index(Model model, Pageable pageable) {

		// 商品一覧表示の並び順を「売れ筋順」に初期化
		sortType = "uriage";

		// 商品情報を全件検索(売れ筋順)
		List<Item> itemsPage = itemRepository.findAllByOrderByCountAllDesc();

		if (itemsPage == null) {
			// 商品情報を全件検索(新着順)
			itemsPage = itemRepository.findAllByOrderByInsertDate();
			//新着商品あり：一覧表示の並び順を「新着順」にする
			sortType = "sintyaku";
		}
		// 取得したログイン商品一覧情報を画面表示用一覧オブジェクトにコピー
		List<ItemBean> itemsBean = beanTools.copyEntityListToItemBeanList(itemsPage);
		
		//一覧表示の並び順と画面表示用一覧オブジェクトをリクエストオブジェクトに設定
		model.addAttribute("sortType",sortType);
		model.addAttribute("items", itemsBean);
		
		//トップ画面表示
		return "/index";
	}

	/**
	* 商品一覧表示処理
	*
	* @author ko teiketsu
	* @param id  商品ID
	* @param model  Viewとの値受渡し
	* @return "client/item/detail" 詳細画面 表示
	*/
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET })
	public String showItems(@PathVariable int id, Model model) {
		List<Item> itemsPage = itemRepository.findAllByOrderByCountAllDesc();
		if (itemsPage == null) {
			// 商品情報を全件検索(新着順)
			itemsPage = itemRepository.findAllByOrderByInsertDate();
		}
		// 商品情報をViewへ渡す	
		return "";
	}

	@RequestMapping(path = "/client/item/list/{sortType}?categoryId={カテゴリID}", method = { RequestMethod.GET })
	public String showItemss(@PathVariable int id, Model model) {
		// 商品情報をViewへ渡す
		return "";
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