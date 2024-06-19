package jp.co.sss.shop.controller.client.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.ReviewRepository;
import jp.co.sss.shop.repository.UserRepository;
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
	 * レビュー情報
	 */
	@Autowired
	ReviewRepository reviewRepository;

	/** 
	 * 注文詳細情報
	 */
	@Autowired
	OrderItemRepository orderItemRepository;

	/** 
	 * 会員情報
	 */
	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * 並び順 
	 */
	int sortType;

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
		sortType = 2;

		// 商品情報を全件検索(売れ筋順)
		List<Item> itemsList = itemRepository.findAllByOrderByCountAllDesc();

		if (itemsList == null) {
			// 商品情報を全件検索(新着順)
			itemsList = itemRepository.findAllByOrderByInsertDate();
			//新着商品あり：一覧表示の並び順を「新着順」にする
			sortType = 1;
		}
		// 取得したログイン商品一覧情報を画面表示用一覧オブジェクトにコピー
		List<ItemBean> itemsBean = beanTools.copyEntityListToItemBeanList(itemsList);

		//画面表示用一覧オブジェクトをリクエストオブジェクトに設定
		model.addAttribute("items", itemsBean);

		//トップ画面表示
		return "/index";
	}

	/**
	* 商品一覧表示処理
	*
	* @author ko teiketsu ryosuke sakagami
	* @param id  商品ID
	* @param model  Viewとの値受渡し
	* @return "client/item/detail" 詳細画面 表示
	*/
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET })
	public String showItems(@PathVariable int sortType, Integer categoryId, Model model) {

		if (categoryId == null) {
			categoryId = 0;
		}

		this.sortType = sortType;
		// 商品格納用のリストを作成
		List<Item> itemsList = new ArrayList<Item>();

		// カテゴリ指定があるかを確認
		if (categoryId != 0) {
			// 検索用カテゴリエンティティを作成
			Category category = new Category();
			category.setId(categoryId);

			// 並び順、カテゴリを条件に商品一覧表示を取得
			if (this.sortType == 1) {
				itemsList = itemRepository.findByCategoryOrderByInsertDate(category);
			} else if (this.sortType == 2) {
				itemsList = itemRepository.findByCategoryByOrderByCountAllDesc(category);
			}
		} else {
			// 並び順を元に商品一覧を取得
			if (this.sortType == 1) {
				// 商品情報を全件検索(新着順)
				itemsList = itemRepository.findAllByOrderByInsertDate();
			} else if (this.sortType == 2) {
				// 商品情報を全件検索(売れ筋順)
				itemsList = itemRepository.findAllByOrderByCountAllDesc();
			}
		}

		// 取得したログイン商品一覧情報を画面表示用一覧オブジェクトにコピー
		List<ItemBean> itemsBean = beanTools.copyEntityListToItemBeanList(itemsList);

		//一覧表示の並び順と画面表示用一覧オブジェクトをリクエストオブジェクトに設定
		model.addAttribute("sortType", sortType);
		model.addAttribute("items", itemsBean);
		model.addAttribute("categoryId", categoryId);

		// 商品情報をViewへ渡す	
		return "/client/item/list";
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
	public String showItem(@ModelAttribute ReviewForm reviewForm, @PathVariable int id, Model model) {

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

		// レビュー格納用のリストを作成
		List<Review> reviewList = new ArrayList<Review>();

		// レビューを全件検索(新着順)
		reviewList = reviewRepository.findByItemOrderByInsertDateDesc(item);

		// レビューが存在する場合、Viewへ渡す
		if (!reviewList.isEmpty()) {
			model.addAttribute("reviews", reviewList);
		}
		// 購入確認フラグを作成
		boolean buyFlg;

		// ログイン済みの場合
		User user = new User();
		if (session.getAttribute("user") != null) {
			// 購入済み確認用会員情報を作成
			UserBean userBean = (UserBean) session.getAttribute("user");
			user = userRepository.getReferenceById(userBean.getId());

			// 購入済みかを確認
			if (!orderItemRepository.findByUserAndItemAndDeleteFlag(user, item).isEmpty()) {
				// 購入済みの場合
				buyFlg = true;

				// レビュー投稿済かを確認
				Review review = reviewRepository.findByUserAndItem(user, item);
				if (review != null) {

					//投稿済の場合レビューフォームに値をセット
					reviewForm.setName(review.getName());
					reviewForm.setInsertDate(review.getInsertDate());
					reviewForm.setEvaluation(review.getEvaluation());
					reviewForm.setCommentReview(review.getCommentReview());
					model.addAttribute("reviewForm", reviewForm);
				}
			} else {
				buyFlg = false;
			}
		} else {
			buyFlg = false;
		}

		// 購入情報をViewへ渡す
		model.addAttribute("buyFlg", buyFlg);

		return "client/item/detail";
	}

	//入力チェックメソッド
}