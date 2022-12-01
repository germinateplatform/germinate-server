package jhi.germinate.resource;

import jhi.germinate.server.database.codegen.enums.UserfeedbackSeverity;

public class UserFeedbackCaptcha
{
	private String               captcha;
	private String               content;
	private String               image;
	private String               pageUrl;
	private String               contactEmail;
	private UserfeedbackSeverity severity;

	public String getCaptcha()
	{
		return captcha;
	}

	public UserFeedbackCaptcha setCaptcha(String captcha)
	{
		this.captcha = captcha;
		return this;
	}

	public String getContent()
	{
		return content;
	}

	public UserFeedbackCaptcha setContent(String content)
	{
		this.content = content;
		return this;
	}

	public String getImage()
	{
		return image;
	}

	public UserFeedbackCaptcha setImage(String image)
	{
		this.image = image;
		return this;
	}

	public String getPageUrl()
	{
		return pageUrl;
	}

	public UserFeedbackCaptcha setPageUrl(String pageUrl)
	{
		this.pageUrl = pageUrl;
		return this;
	}

	public String getContactEmail()
	{
		return contactEmail;
	}

	public UserFeedbackCaptcha setContactEmail(String contactEmail)
	{
		this.contactEmail = contactEmail;
		return this;
	}

	public UserfeedbackSeverity getSeverity()
	{
		return severity;
	}

	public UserFeedbackCaptcha setSeverity(UserfeedbackSeverity severity)
	{
		this.severity = severity;
		return this;
	}
}
