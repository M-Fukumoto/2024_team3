package jp.co.sss.shop.controller.client.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
@Controller
public class ClientOrderRegistController {
	
	/**
	 * 注文情報
	 */
	@Autowired
	OrderRepository orderRepository;
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
	
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String addressInput() { 
		//セッションスコープからログイン会員情報を取得
		//OrderForm orderform = (OrderForm)
		//取得したログイン会員情報のユーザIDを条件にDBからユーザ情報を取得
		
		//取得したユーザ情報を注文入力フォーム情報に設定
		
		//注文入力フォーム情報の支払方法に初期値としてクレジットカードを設定
		
		//注文入力フォーム情報をセッションスコープに保存
		

		return "/client/order/address/input";
	}
	
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String addressInput(Model model) {
		
		//セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		//注文入力フォーム情報をリクエストスコープに設定
		session.setAttribute("orderForm", orderForm);
		
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			//セッションにエラー情報がある場合、エラー情報をスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			// セッションにエラー情報を削除
			session.removeAttribute("result");
		}
		
		
		return "client/order/address_inpu";
	}
}
