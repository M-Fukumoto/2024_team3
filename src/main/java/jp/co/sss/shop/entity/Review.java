package jp.co.sss.shop.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	private String commentReview;
	
	/**
	 * アイテムID
	 */
	@ManyToOne
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;
	/**
	 * ユーザーID
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	/**
	 * 投稿日付
	 */
	@Column(insertable = false)
	private Date insertDate;
	/**
	 * 削除フラグ
	 */
	@Column(insertable = false)
	private Integer deleteFlag;
	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id セットする id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name セットする name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return evaluation
	 */
	public Integer getEvaluation() {
		return evaluation;
	}
	/**
	 * @param evaluation セットする evaluation
	 */
	public void setEvaluation(Integer evaluation) {
		this.evaluation = evaluation;
	}
	/**
	 * @return commentReview
	 */
	public String getCommentReview() {
		return commentReview;
	}
	/**
	 * @param commentReview セットする commentReview
	 */
	public void setCommentReview(String commentReview) {
		this.commentReview = commentReview;
	}
	/**
	 * @return insertDate
	 */
	public Date getInsertDate() {
		return insertDate;
	}
	/**
	 * @param insertDate セットする insertDate
	 */
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}
	/**
	 * @return deleteFlag
	 */
	public Integer getDeleteFlag() {
		return deleteFlag;
	}
	/**
	 * @param deleteFlag セットする deleteFlag
	 */
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	/**
	 * @return item
	 */
	public Item getItem() {
		return item;
	}
	/**
	 * @param item セットする item
	 */
	public void setItem(Item item) {
		this.item = item;
	}
	/**
	 * @return user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user セットする user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
}
