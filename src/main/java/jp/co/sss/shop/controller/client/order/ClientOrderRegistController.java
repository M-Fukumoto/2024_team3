package jp.co.sss.shop.controller.client.order;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Order;
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
	
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String redirectInputAddress() {
		return "/client/order/address/input";
	}

	/*@RequestMapping(path = "/order/address/input", method = RequestMethod.GET)
	public String inputAddress(OrderForm form, Model model) {

		Order order = new Order();
		BeanUtils.copyProperties(form, order, "id");
		order = OrderRepository.save(order);

		return "client/order/address_input";
	}
	
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInput(OrderForm form, Model model) {
		Order order = new Order();
		BeanUtils.copyProperties(form, order, "id");
		order = repository
	}*/

}
