package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
	List<Review> findByItemAndDeleteFlagOrderByInsertDateDesc(Item item,Integer DdeleteFlag);
	
	/**
	 * ユーザとアイテムを条件にレビューを取得
	 * @param user
	 * @return
	 */
	Review findByUserAndItemAndDeleteFlag(User user,Item item,Integer deleteFlag);
	
	
	/**
	 * アイテムを条件にレビューを3件取得
	 * @author ryosuke sakagami
	 * @param item
	 * @return
	 */
	@Query("SELECT r FROM Review r WHERE deleteFlag = 0 AND Item = :item AND ROWNUM <= 3")
	List<Review> findByDeleteFlagAndItemAnd3Rownum(Item item);
	
	/**
	 * 評価取り出し
	 * @param item
	 * @return
	 */
	@Query("SELECT AVG(r.evaluation) FROM Review r WHERE Item = :item")
	Review findByItemAvgEvaluation(Item item);
}
