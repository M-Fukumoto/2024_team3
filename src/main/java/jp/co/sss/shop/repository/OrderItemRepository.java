package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;

/**
 * order_itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	
	@Query("SELECT oi FROM OrderItem oi INNER JOIN Order o ON oi.order.id = o.id WHERE o.user = :user AND oi.item = :item" )
	List<OrderItem> findByUserAndItemAndDeleteFlag(User user,Item item);
}
