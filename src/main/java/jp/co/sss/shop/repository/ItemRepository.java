package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.util.JPQLConstant;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**
	 * 商品情報を登録日付順に取得 管理者機能で利用
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	@Query(JPQLConstant.FIND_ALL_ITEMS_ORDER_BY_INSERT_DATE)
	Page<Item> findByDeleteFlagOrderByInsertDateDescPage(
	        @Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 商品IDと削除フラグを条件に検索（管理者機能で利用）
	 * @param id 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByIdAndDeleteFlag(Integer id, int deleteFlag);

	/**
	 * 商品名と削除フラグを条件に検索 (ItemValidatorで利用)
	 * @param name 商品名
	 * @param notDeleted 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByNameAndDeleteFlag(String name, int notDeleted);
	
	/**
	 * 商品を売れ筋順に表示
	 * @author sakagami ryosuke
	 * @return 商品エンティティ
	 */
	@Query("SELECT i FROM Item i INNER JOIN OrderItem o ON i.id = o.item.id WHERE i.deleteFlag = 0 GROUP BY i order by COUNT(i.id) DESC")
	List<Item> findAllByOrderByCountAllDesc();
	
	/**
	 * 商品を新着順に表示
	 * @author sakagami ryosuke
	 * @return 商品エンティティ
	 */
	List<Item> findAllByOrderByInsertDate();
}