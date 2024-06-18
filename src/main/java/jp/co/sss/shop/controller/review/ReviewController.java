package jp.co.sss.shop.controller.review;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.bean.ReviewBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.ReviewRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

@Controller
public class ReviewController {
	@Autowired
	ItemRepository itemRepository;
	@Autowired
	ReviewRepository reviewRepository;

	@Autowired
	BeanTools beanTools;

	
	@RequestMapping("/client/item/review/test")
	public String reviewTest(Model model) {
		Item item = itemRepository.findByIdAndDeleteFlag(1, Constant.NOT_DELETED);

		if (item == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		//Itemエンティティの各フィールドの値をItemBeanにコピー
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);

		// 商品情報をViewへ渡す
		model.addAttribute("item", itemBean);

		return "/client/review/list";
	}

	/**
	* レビュー機能一覧表示処理
	*
	* @author ko teiketsu
	* @return "/client/review/review" 詳細画面 表示
	*/
	@RequestMapping(path = "/client/item/review/{id}", method = { RequestMethod.GET })
	public String reviewAll(@PathVariable Integer id,Model model) {
	// 外部参照先テーブルに対応付けられたエンティティItemのオブジェクトを生成
	Item item = new Item();
	//Itemのオブジェクト内のidフィールドにパラメータの値を代入
	item.setId(id);
	//itemのオブジェクト内のidフィールドを使用した条件検索を実行
	//List<Review> review = reviewRepository.findItemByOrderByInsertDateDesc(item);
	// レビュー格納用のリストを作成
	List<Review> reviewList = new ArrayList<Review>();
	// レビューを全件検索(新着順)
	reviewList = reviewRepository.findByItemOrderByInsertDateDesc(item);
	//Itemエンティティの各フィールドの値をItemBeanにコピー
	List<ReviewBean> reivewBean = new ArrayList<ReviewBean>();
	BeanUtils.copyProperties(reviewList, reivewBean);
	// レビューをViewへ渡す
	model.addAttribute("review", reviewList);
	//商品詳細画面に遷移
	return "/client/review/list";
	}
}
