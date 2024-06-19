package jp.co.sss.shop.controller.client.order;
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
import jp.co.sss.shop.bean.OrderBean;
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
	 * Entity、Form、Bean間のデータ生成、コピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	/**
	 * 合計金額計算サービス
	 */
	@Autowired
	PriceCalc priceCalc;
	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;
	/**
	 * 会員情報 リポジトリ
	 */
	@Autowired
	UserRepository userRepository;
	/**
	 * 注文情報ー リポジトリ
	 */
	@Autowired
	OrderRepository orderRepository;
	/**
	 * 商品情報 リポジトリ
	 */
	@Autowired
	ItemRepository itemRepository;
	/**
	 * 注文商品情報 リポジトリ
	 */
	@Autowired
	OrderItemRepository orderItemRepository;
	/**
	 * ご注文のお手続きボタン 押下時処理
	 * @return "redirect:/client/order/address/input" 届け先入力画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String orderAddressInputRedirect() {
		//注文入力フォーム情報を作成
		OrderForm orderForm = new OrderForm();
		//セッションスコープからログイン会員情報を取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		//取得したログイン会員情報のユーザIDを条件にDBからユーザ情報を取得
		User user = userRepository.getReferenceById(userBean.getId());
		//取得したユーザ情報を注文入力フォーム情報に設定
		orderForm.setId(user.getId());
		orderForm.setName(user.getName());
		orderForm.setPostalCode(user.getPostalCode());
		orderForm.setAddress(user.getAddress());
		orderForm.setPhoneNumber(user.getPhoneNumber());
		//注文入力フォーム情報の支払方法に初期値としてクレジットカードを設定
		orderForm.setPayMethod(1);
		//注文入力フォーム情報をセッションスコープに保存
		session.setAttribute("orderForm", orderForm);
		//届け先入力画面表示処理へリダイレクト
		return "redirect:/client/order/address/input";
	}
	/**
	 * 届け先入力画面表示処理
	 * @param model viewとの値受け渡し
	 * @return "client/order/address_input" 登録画面 表示
	 */
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
	/**
	 * 届け先入力画面 次へボタン 押下時処理
	 * @param orderForm 注文フォーム情報
	 * @param result 入力エラー情報取得
	 * @param model viewとの値受け渡し
	 * @return "redirect:/client/order/payment/input" 支払方法選択画面表示処理にリダイレクト
	 */
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
	};
	/**
	 * 支払方法選択画面表示処理
	 * @param mdoel   Viewとの値受渡し
	 * return "client/order/payment_input"  支払方法選択画面 表示
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String orderPaymentInput(Model model) {
		//セッションスコープから注文入力フォーム情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		//注文フォーム情報をリクエストスコープに設定
		model.addAttribute("orderForm", orderForm);
		//支払方法選択画面表示
		return "client/order/payment_input";
	}
	/**
	 * 支払方法選択画面 次へボタン 押下時処理
	 * @param payMethod 支払方法
	 * @return  "redirect:/client/order/check" 注文確認画面表示処理へリダイレクト
	 */
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
	/**
	 *
	 * @param model  Viewとの値受渡し
	 * @return "client/order/check" 注文確認画面 表示
	 */
	/*@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheck(Model model) {
		//セッションスコープから注文情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		//セッションスコープから買い物かご情報を取得
		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		//注文商品の最新情報をDBから取得し、在庫チェックをする
		Iterator<BasketBean> iterator = basketBeans.iterator();
		//注文警告メッセージ管理用のListを作成
		List<Item> stockError = new ArrayList<>();
		while (iterator.hasNext()) {
			BasketBean basket = iterator.next();
			Item item = itemRepository.getReferenceById(basket.getId());
			//在庫切れ商品がある場合
			if (item.getStock() == 0) {
				//注文警告メッセージをリクエストスコープに保存
				stockError.add(item);
				String stockNone = "msg.order.item.stock.none";
				model.addAttribute("stockNone", stockNone);
				//在庫切れの商品は、買い物かごから情報を削除
				iterator.remove();
				//在庫不足商品がある場合
			} else if (item.getStock() < basket.getOrderNum()) {
				//在庫数にあわせて、買い物かご情報を更新（注文数、在庫数）
				basket.setOrderNum(item.getStock());
				//注文警告メッセージをリクエストスコープに保存
				stockError.add(item);
				String stockShort = "msg.order.item.stock.short";
				model.addAttribute("stockShort", stockShort);
			}
		}
		//注文警告メッセージをリクエストスコープに保存
		model.addAttribute("stockError", stockError);
		//在庫状況を反映した買い物かご情報をセッションに保存
		session.setAttribute("basketBeans", basketBeans);
		//買い物かご情報から、商品ごとの金額小計と全額を算出し、注文入力フォーム情報に設定
		if (basketBeans != null || basketBeans.size() != 0) {
			//小計
			List<OrderItemBean> orderItemBeanList = new ArrayList<>();
			for (BasketBean basketBean : basketBeans) {
				Item item = itemRepository.getReferenceById(basketBean.getId());
				OrderItemBean orderItemBean = beanTools.generateOrderItemBean(item, basketBean);
				orderItemBeanList.add(orderItemBean);
			}
			//全額
			Integer total = priceCalc.orderItemBeanPriceTotalUseSubtotal(orderItemBeanList);
			Integer userId = (Integer) ((UserBean) session.getAttribute("user")).getId();
			OrderBean orderBean = beanTools.copyFormToOrderBean(orderForm, total, userId);
			session.setAttribute("order", orderBean);
			//注文入力フォーム情報をリクエストスコープに設定
			model.addAttribute("orderBean", orderBean);
			model.addAttribute("orderItemBean", orderItemBeanList);
		}
		//注文確認画面表示
		return "client/order/check";
	}*/
	/**
	 * 支払方法選択画面で、戻るボタン押下処理
	 * @return "redirect:/client/order/address/input" 登録画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String orderPyamentBackRedirect() {
		//支払い方法選択画面 表示処理へリダイレクト
		return  "redirect:/client/order/address/input";
	}
	/**
	 * ご注文の確定ボタン 押下時処理
	 * @return "redirect:/client/order/complete" 注文完了画面表示処理 リダイレクト
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderCompleteRedirect() {
		//セッションスコープから注文情報を取得
		OrderBean orderBean = (OrderBean) session.getAttribute("order");
		//セッションスコープから買い物かご情報を取得
		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		//注文商品の在庫チェックをする
		for (BasketBean basketBean : basketBeans) {
			Item item = itemRepository.getReferenceById(basketBean.getId());
			//在庫切れ商品がある場合
			if (item.getStock() == 0) {
				//注文確認画面表示処理へリダイレクト
				return "redirect:/client/order/check";
			}
		}
		//注文情報を元にDB登録用エンティティオブジェクトを生成
		Order order = new Order();
		//注文テーブルのDB登録実施
		BeanUtils.copyProperties(orderBean, order, "id");
		Integer userId = (Integer) ((UserBean) session.getAttribute("user")).getId();
		User user = new User();
		user.setId(userId);
		order.setUser(user);
		order = orderRepository.save(order);
		//注文商品テーブルのDB登録実施
		for (BasketBean basketBean : basketBeans) {
			//注文情報を元にDB登録用エンティティオブジェクトを生成
			OrderItem orderItem = new OrderItem();
			Item item = itemRepository.getReferenceById(basketBean.getId());
			orderItem.setQuantity(basketBean.getOrderNum());
			orderItem.setOrder(order);
			orderItem.setItem(item);
			orderItem.setPrice(item.getPrice());
			orderItem = orderItemRepository.save(orderItem);
		}
		//セッションスコープの注文入力フォーム情報と買い物かご情報を削除
		session.removeAttribute("orderForm");
		session.removeAttribute("order");
		session.removeAttribute("basketBeans");
		//注文完了画面表示処理にリダイレクト
		return "redirect:/client/order/complete";
	}
	/**
	 * 注文完了画面表示処理
	 * @return "client/order/complete" 注文完了画面 表示
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderComplete() {
		return "client/order/complete";
	}
}
