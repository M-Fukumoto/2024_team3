package jp.co.sss.shop.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * レビュー入力フォーム
 *
 * @author ko teiketsu
 *
 * TIPS 入力チェックアノテーションのmessage属性に"{messages.propertiesで指定した名前}"と記述することができます。
 * 
 */
public class ReviewForm {
	/**
	 * 投稿者名
	 */
	@NotBlank
	@Size(min = 1, max = 40, message = "{text.maxsize.message}")
	private String	name;
	/**
	 * 評価
	 */
	private Integer evaluation;
	/**
	 * コメント
	 */
	@Size(min = 0, max = 400, message = "{text.maxsize.message}")
	private String commentReview;


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
}
	