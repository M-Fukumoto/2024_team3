package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
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
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

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
	 * 合計金額計算サービス
	 */
	@Autowired
	PriceCalc priceCalc;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

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

		//届け先入力画面表示処理へリダイレクト
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
		session.setAttribute("orderForm", orderForm);
		//BindingResultオブジェクトに入力エラー情報がある場合
		if (result.hasErrors()) {
			//入力エラー情報をセッションスコープに設定
			session.setAttribute("result", result);
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
		
		orderForm.setPayMethod(payMethod);
		
		//注文入力フォーム情報をセッションスコープに保存
		session.setAttribute("orderForm", orderForm);
		//注文確認画面表示処理へリダイレクト
		return "redirect:/client/order/check";
	}

	/**
	 * 注文詳細確認
	 * @author ryosuke sakagami
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheck(Model model) {
		// セッションスコープから注文情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		
		// セッションスコープから買い物かご情報を取得
		basket = (List<BasketBean>) session.getAttribute("basket");

		// 注文商品の最新情報をDBから取得し、在庫チェックをする
		// 在庫切れ商品リスト作成
		List<String> stockLackItems = new ArrayList<String>();
		// 在庫ゼロ商品リストの作成
		List<String> stockZeroItems = new ArrayList<String>();
		
		// 買い物かごの回数分繰り返し
		for (int i = 0; i < basket.size(); i++) {
			// カート内アイテムの取り出し
			BasketBean bB = basket.get(i);
			// 該当アイテム詳細を取り出し
			Item item = itemRepository.getReferenceById(bB.getId());
			// 在庫状況を更新
			bB.setStock(itemRepository.getReferenceById(bB.getId()).getStock());

			// 注文数が在庫数を比較
			if (bB.getOrderNum() > bB.getStock()) {
				// 在庫が0の場合
				if (bB.getStock() == 0) {
					// 在庫切れ商品名をリストに追加
					stockZeroItems.add(bB.getName());
					// 在庫切れの商品は、買い物かごか情報ら削除
					basket.remove(i);
					// 添え字を調整
					i--;
				}
				// 在庫が不足している場合
				else {
					// 在庫不足商品名をリストに追加
					stockLackItems.add(bB.getName());
					// 在庫数にあわせて、買い物かご情報を更新（注文数、在庫数）
					bB.setOrderNum(bB.getStock());
					// リストに保存
					basket.set(i, bB);
				}
			} else {
				// リストに保存
				basket.set(i, bB);
			}

		}

		// 在庫状況を反映した買い物かご情報をセッションに保存
		session.setAttribute("basket", basket);

		// 買い物かご情報から、商品ごとの金額小計と全額を算出し、注文入力フォーム情報に設定
		// 画面表示用注文詳細リストを作成
		List<OrderItemBean> orderItemBeanList = new ArrayList<OrderItemBean>();
		// 金額計算用注文詳細を作成
		OrderItemBean orderItemBean = new OrderItemBean();
		// 合計金額を可能
		Integer total = 0;
		for (int i = 0; i < basket.size(); i++) {
			// カート内アイテムの取り出し
			BasketBean bB = basket.get(i);
			// 該当アイテム詳細を取り出し
			Item item = itemRepository.getReferenceById(bB.getId());
			// 買い物かごの商品とアイテムデータから画面表示用注文詳細を作成
			orderItemBean = beanTools.generateOrderItemBean(item, bB);
			// リストに追加
			orderItemBeanList.add(orderItemBean);
			// 合計金額を計算
			total += orderItemBean.getSubtotal();
		}
		// 注文入力フォーム情報をリクエストスコープに設定
		model.addAttribute("orderItemBeans", orderItemBeanList);
		
		// 合計金額をリクエストスコープに保存
		model.addAttribute("total", total);
		
		// 注文警告メッセージをリクエストスコープに保存
		model.addAttribute("stockZeroItems", stockZeroItems);
		model.addAttribute("stockLackItems", stockLackItems);
		
		// 注文確認画面表示
		return "client/order/check";
	}

	/**
	 * 支払い選択画面に戻る
	 * @author sakagami ryosuke
	 * @return
	 */
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String orderPaymentBack() {
		// 支払い方法選択画面 表示処理へリダイレクト
		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderCompleteRedirect() {
		// セッションスコープから注文情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		// セッションスコープから買い物かご情報を取得
		basket = (List<BasketBean>) session.getAttribute("basket");
		// 金額計算用注文詳細を作成
		OrderItemBean orderItemBean = new OrderItemBean();
		
		// 買い物かごの回数分繰り返し
		for (int i = 0; i < basket.size(); i++) {
			// カート内アイテムの取り出し
			BasketBean bB = basket.get(i);
			// 該当アイテム詳細を取り出し
			Item item = itemRepository.getReferenceById(bB.getId());
			// 在庫状況を更新
			bB.setStock(itemRepository.getReferenceById(bB.getId()).getStock());
			// 注文数が在庫数を比較
			if (bB.getOrderNum() > bB.getStock()) {
				// 在庫が不足している場合、確認画面にリダイレクト
				return "redirect:/client/order/check";
			}
		}
		
		// 注文情報情報を元にDB登録用エンティティオブジェクトを生成
		Order order = new Order();
		// 注文フォームからエンティティに内容をコピー
		BeanUtils.copyProperties(orderForm, order,"id");
		// ユーザ情報をセット
		UserBean userBean = (UserBean) session.getAttribute("user");
		User user = userRepository.getReferenceById(userBean.getId());
		order.setUser(user);
		// 注文情報をDBに登録
		Order newOrder = orderRepository.save(order);
		

		// 注文商品情報の登録処理
		// 注文商品エンティティの作成
		OrderItem orderItem = new OrderItem();
		// 注文リストを作成
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for (int i = 0; i < basket.size(); i++) {
			// カート内アイテムの取り出し
			BasketBean bB = basket.get(i);
			// 該当アイテム詳細を取り出し
			Item item = itemRepository.getReferenceById(bB.getId());
			
			// 買い物かごの商品とアイテムデータから画面表示用注文詳細を作成
			orderItemBean = beanTools.generateOrderItemBean(item, bB);
			// エンティティにコピー
			BeanUtils.copyProperties(orderItemBean, orderItem,"id");
			// リストに追加
			orderItemList.add(orderItem);
			// 注文情報をセット
			orderItem.setOrder(newOrder);
			// 商品をセット
			orderItem.setItem(item);
			// 注文数をセット
			orderItem.setQuantity(orderItemBean.getOrderNum());
			// DBに登録
			orderItemRepository.save(orderItem);
			
			// 商品の在庫数を更新
			item.setStock(item.getStock()- orderItemBean.getOrderNum());
			itemRepository.save(item);
		}

		
		// セッションスコープの注文入力フォーム情報と買い物かご情報を削除
		session.removeAttribute("basket");
		session.removeAttribute("orderForm");
		
		// 注文完了画面表示処理にリダイレクト
		return "redirect:/client/order/complete";
	}
	
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderComplete() {
		//注文完了画面表示
		return "client/order/complete";
	}

}