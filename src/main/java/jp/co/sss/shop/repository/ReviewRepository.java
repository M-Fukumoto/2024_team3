package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.entity.User;

/**
 * reviewテーブル用リポジトリ
 *
 * @author ko teiketsu
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
	
	/**
	 * レビュー情報を登録日付順に取得
	 * @author ko teiketsu
	 */
	List<Review> findByItemOrderByInsertDateDesc(Item item);
	
	/**
	 * ユーザとアイテムを条件にレビューを取得
	 * @param user
	 * @return
	 */
	Review findByUserAndItem(User user,Item item);
}
