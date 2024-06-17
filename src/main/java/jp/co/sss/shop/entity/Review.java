package jp.co.sss.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * レビュー機能エンティティクラス
 *
 * @author ko teiketsu
 */
@Entity
@Table(name = "review")
public class Review {
	/**
	 * レビューID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_review_gen")
	@SequenceGenerator(name = "seq_review_gen", sequenceName = "seq_review", allocationSize = 1)
	private Integer id;
	/**
	 * 投稿者名
	 */
	@Column
	private String	name;
	/**
	 * 評価
	 */
	@Column
	private Integer evaluation;
	/**
	 * コメント
	 */
	@Column
	private String comment_review;
	public Integer getId() {
	/**
	 * レビューIDの取得
	 * @return レビューID
	 */
		return id;
	}
	/**
	 * レビューIDのセット
	 * @param id レビューID
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 投稿者名の取得
	 * @return 投稿者名
	 */
	public String getName() {
		return name;
	}
	/**
	 * 投稿者名のセット
	 * @param name 投稿者名
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 評価の取得
	 * @return 評価
	 */
	public Integer getEvaluation() {
		return evaluation;
	}
	/**
	 * 評価のセット
	 * @param evaluation 評価
	 */
	public void setEvaluation(Integer evaluation) {
		this.evaluation = evaluation;
	}
	/**
	 * コメントの取得
	 * @return コメント
	 */
	public String getComment_review() {
		return comment_review;
	}
	/**
	 * コメントのセット
	 * @param comment_review コメント
	 */
	public void setComment_review(String comment_review) {
		this.comment_review = comment_review;
	}
}
