package jp.co.sss.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Review;

/**
 * reviewテーブル用リポジトリ
 *
 * @author ko teiketsu
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

}
