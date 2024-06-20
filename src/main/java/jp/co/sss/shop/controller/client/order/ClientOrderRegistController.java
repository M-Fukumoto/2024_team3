package jp.co.sss.shop.controller.client.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BeanTools;
@Controller
public class ClientOrderRegistController {
	
	/**
	 * 注文情報
	 */
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	UserRepository userRepository;
	/**
	 * Entity、Form、Bean間のデータ生成、コピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	
	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;
	@Autowired
	ItemRepository iR;
	List<BasketBean> basket;
	
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	//- 注文入力フォーム情報を作成
	public String addressInput(@ModelAttribute OrderForm orderForm) { 
		
		//セッションスコープからログイン会員情報を取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		
		//取得したログイン会員情報のユーザIDを条件にDBからユーザ情報を取得
		User user = userRepository.getReferenceById(userBean.getId());
		
		//取得したユーザ情報を注文入力フォーム情報に設定
		orderForm.setPostalCode(user.getPostalCode());
		orderForm.setAddress(user.getAddress());
		orderForm.setName(user.getName());
		orderForm.setPhoneNumber(user.getPhoneNumber());
		
		//注文入力フォーム情報の支払方法に初期値としてクレジットカードを設定
		orderForm.setPayMethod(1);
		
		//注文入力フォーム情報をセッションスコープに保存
		session.setAttribute("orderForm", orderForm);
		
		//・届け先入力画面表示処理へリダイレクト
		return "redirect:/client/order/address/input";
	}
	
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String orderAddressInput(Model model) {
		//セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		//注文入力フォーム情報をリクエストスコープに設定
		model.addAttribute("orderForm", orderForm);
		//セッションスコープに入力エラー情報がある場合
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			//取得したエラー情報をリクエストスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.orderForm", result);
			//セッションスコープから、エラー情報を削除
			session.removeAttribute("result");
		}
		//登録画面表示
		return "client/order/address_input";
	}
	
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String orderPaymentInputRedirect(@Valid @ModelAttribute OrderForm orderForm, BindingResult result,
			Model model) {
		//リクエストスコープに画面から入力されたフォーム情報を注文入力フォーム情報として保存
		session.setAttribute("orderForm",orderForm);
		//BindingResultオブジェクトに入力エラー情報がある場合
		if (result.hasErrors()) {
			//入力エラー情報をセッションスコープに設定
			session.setAttribute("result",result);
			//届け先入力画面表示処理にリダイレクト
			return "redirect:/client/order/address/input";
		} else {
			//入力エラーがない場合
			//支払方法選択画面表示処理にリダイレクト
			return "redirect:/client/order/payment/input";
		}
	}
	
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String orderPaymentInput(Model model) {
		//セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		//注文フォーム情報をリクエストスコープに設定
		model.addAttribute("orderForm", orderForm);
		//支払方法選択画面表示
		return "client/order/payment_input";
	}
	
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheckRedirect(Integer payMethod) {
		//セッションスコープから注文入力フォームを取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		//画面から入力された支払方法を取得した注文入力フォーム情報に設定
		orderForm.setPayMethod(payMethod);
		//注文入力フォーム情報をセッションスコープに保存
		session.setAttribute("orderForm", orderForm);
		//注文確認画面表示処理へリダイレクト
		return "redirect:/client/order/check";
	}
	
	/*@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String check2(Model model) {
		//・セッションスコープから注文情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		//・セッションスコープから買い物かご情報を取得
		List<BasketBean> basketBean = (List<BasketBean>) session.getAttribute("basketBean");
		
		//・注文商品の最新情報をDBから取得し、の在庫チェックをする
		for (int i = 0; i < basketBean.size(); i++) {
			// カート内アイテムの取り出し
			BasketBean bB = basketBean.get(i);
			// 在庫状況を更新
			bB.setStock(iR.getReferenceById(bB.getId()).getStock());
			// セッションに買い物かごデータを代入
			session.setAttribute("basketBean", basketBean);
		}
				
		//取得したログイン会員情報のユーザIDを条件にDBからユーザ情報を取得
		User user = userRepository.getReferenceById(userBean.getId());
		//・在庫不足、在庫切れ商品がある場合
		if(Objects.nonNull(basket)) {
			// 在庫数を最新の状態に更新
			for (int i = 0; i < basket.size(); i++) {
				// カート内アイテムの取り出し 商品の個数　在庫数を比較
				BasketBean bB = basket.get(i);
		basket = (List<BasketBean>) session.getAttribute("basket");
		//- 注文警告メッセージをリクエストスコープに保存
		
		//- 在庫数にあわせて、買い物かご情報を更新（注文数、在庫数）
		
		//- 在庫切れの商品は、買い物かごか情報ら削除
		
		//・在庫状況を反映した買い物かご情報をセッションに保存
		
		//・買い物かご情報から、商品ごとの金額小計と全額を算出し、注文入力フォーム情報に設定
		
		//・注文入力フォーム情報をリクエストスコープに設定
		model.addAttribute("orderForm", orderForm);
		//・注文確認画面表示
		return "client/order/check";
	}
	
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String paymentBack() {
		//・支払い方法選択画面 表示処理へリダイレクト

		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String complete() {
		//・セッションスコープから注文情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		//・セッションスコープから買い物かご情報を取得
		BasketBean basketBean = (BasketBean) session.getAttribute("basket");
		//・注文商品の在庫チェックをする
		if(Objects.nonNull(basket)) {
			// 在庫数を最新の状態に更新
			for (int i = 0; i < basket.size(); i++) {
				// カート内アイテムの取り出し 商品の個数　在庫数を比較
				BasketBean bB = basket.get(i);
				
		//・在庫切れ商品がある場合
		
		//-注文確認画面表示処理へリダイレクト
		//リダイレクト:” /client/order/check” 
		return "/client/order/check";
		
		//・注文情報情報を元にDB登録用エンティティオブジェクトを生成
		
		//・注文テーブルおよび注文商品テーブルのDB登録実施
		
		//・セッションスコープの注文入力フォーム情報と買い物かご情報を削除
		
		//・注文完了画面表示処理にリダイレクト
		return "redirect:/client/order/complete";
	}
	
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String complete2() {
		//・注文完了画面表示
		return "client/order/complete";
	}@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String orderPyamentBackRedirect() {
		//支払い方法選択画面 表示処理へリダイレクト
		return  "redirect:/client/order/address/input";
	}*/


}