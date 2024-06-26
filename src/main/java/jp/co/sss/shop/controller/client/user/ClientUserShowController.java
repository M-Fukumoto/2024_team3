package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 会員管理 表示機能(一般会員)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientUserShowController {
	/**
	 * 会員情報　リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * 一覧表示処理
	 *
	 * @param model  Viewとの値受渡し
	 * @param pageable ページング制御
	 * @return "admin/user/list" 一覧画面　表示
	 */
	@RequestMapping(path = "/client/user/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showUserList(Model model, Pageable pageable) {

		// 会員情報の登録数の取得と新規追加可否チェック
		Long usersCount = userRepository.count();
		Boolean registrable = true;
		if (usersCount == Constant.USERS_MAX_COUNT) {
			registrable = false;
		}

		// 会員情報リストを取得
		Integer authority = ((UserBean) session.getAttribute("user")).getAuthority();
		Page<User> userList = userRepository.findUsersListOrderByInsertDate(Constant.NOT_DELETED, authority, pageable);

		// 会員情報をViewに渡す
		model.addAttribute("registrable", registrable);
		model.addAttribute("pages", userList);
		model.addAttribute("users", userList.getContent());
		

		//会員登録・変更・削除用のセッションスコープを初期化
		session.removeAttribute("userForm");

		// 一覧表示
		return "admin/user/list";
	}

	/**
	 * 詳細表示処理
	 *
	 * @param id 表示対象会員ID
	 * @param model Viewとの値受渡し
	 * @return "admin/user/detail" 会員詳細表示画面へ
	 * 
	 */
	@RequestMapping(path = "/client/user/detail", method = { RequestMethod.GET, RequestMethod.POST })
	public String showUser(Model model) {
		// 表示対象の情報を取得
		Integer id = ((UserBean) session.getAttribute("user")).getId();
		User user = userRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (user == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		// Userエンティティの各フィールドの値をUserBeanにコピー
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);

		// 会員情報をViewに渡す
		model.addAttribute("userBean", userBean);

		//会員登録・変更・削除用のセッションスコープを初期化
		session.removeAttribute("userForm");

		// 詳細画面　表示
		return "client/user/detail";
	}
}


