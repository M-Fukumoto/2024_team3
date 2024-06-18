package jp.co.sss.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.OrderItem;

/**
 * order_itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	
	/*OrderItem findById(Integer id); 
	List<OrderItem> findAllByOrderItemByInsertDateDescPage(Integer userId);
	
	@Query(JPQLConstant.FIND_ORDERITEMS_LIST_ORDER_BY_INSERT_DATE)
	Page<User> findUsersListOrderByInsertDate(
			@Param("deleteFlag") int deleteFlag, @Param("authority") int authority, Pageable pageable);
			*/
}
