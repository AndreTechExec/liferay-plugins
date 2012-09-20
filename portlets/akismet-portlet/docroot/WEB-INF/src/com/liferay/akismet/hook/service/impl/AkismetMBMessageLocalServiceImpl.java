/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.akismet.hook.service.impl;

import com.liferay.akismet.model.AkismetData;
import com.liferay.akismet.service.AkismetDataLocalServiceUtil;
import com.liferay.akismet.util.AkismetConstants;
import com.liferay.akismet.util.AkismetUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalService;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceWrapper;
import com.liferay.portlet.wiki.model.WikiPage;

import java.io.InputStream;

import java.util.List;
import java.util.Map;

/**
 * @author Amos Fong
 * @author Mika Koivisto
 */
public class AkismetMBMessageLocalServiceImpl
	extends MBMessageLocalServiceWrapper {

	public AkismetMBMessageLocalServiceImpl(
		MBMessageLocalService mbMessageLocalService) {

		super(mbMessageLocalService);
	}

	@Override
	public MBMessage addDiscussionMessage(
			long userId, String userName, long groupId, String className,
			long classPK, long threadId, long parentMessageId, String subject,
			String body, ServiceContext serviceContext)
		throws PortalException, SystemException {

		boolean enabled = isDiscussionsEnabled(userId);

		if (enabled) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		MBMessage message = super.addDiscussionMessage(
			userId, userName, groupId, className, classPK, threadId,
			parentMessageId, subject, body, serviceContext);

		AkismetData akismetData = updateAkismetData(message, serviceContext);

		if (!enabled) {
			return message;
		}

		int status = WorkflowConstants.STATUS_APPROVED;

		if (isSpam(userId, subject, body, akismetData)) {
			status = WorkflowConstants.STATUS_DENIED;
		}

		return super.updateStatus(
			userId, message.getMessageId(), status, serviceContext);
	}

	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			long threadId, long parentMessageId, String subject, String body,
			String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		boolean enabled = isMessageBoardsEnabled(userId);

		if (enabled) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		MBMessage message = super.addMessage(
			userId, userName, groupId, categoryId, threadId, parentMessageId,
			subject, body, format, inputStreamOVPs, anonymous, priority,
			allowPingbacks, serviceContext);

		AkismetData akismetData = updateAkismetData(message, serviceContext);

		if (!enabled) {
			return message;
		}

		int status = WorkflowConstants.STATUS_APPROVED;

		if (isSpam(userId, subject, body, akismetData)) {
			status = WorkflowConstants.STATUS_DENIED;
		}

		return super.updateStatus(
			userId, message.getMessageId(), status, serviceContext);
	}

	@Override
	public MBMessage addMessage(
			long userId, String userName, long groupId, long categoryId,
			String subject, String body, String format,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			boolean anonymous, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		boolean enabled = isMessageBoardsEnabled(userId);

		if (enabled) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		MBMessage message = super.addMessage(
			userId, userName, groupId, categoryId, subject, body, format,
			inputStreamOVPs, anonymous, priority, allowPingbacks,
			serviceContext);

		AkismetData akismetData = updateAkismetData(message, serviceContext);

		if (!enabled) {
			return message;
		}

		int status = WorkflowConstants.STATUS_APPROVED;

		if (isSpam(userId, subject, body, akismetData)) {
			status = WorkflowConstants.STATUS_DENIED;
		}

		return super.updateStatus(
			userId, message.getMessageId(), status, serviceContext);
	}

	@Override
	public MBMessage updateDiscussionMessage(
			long userId, long messageId, String className, long classPK,
			String subject, String body, ServiceContext serviceContext)
		throws PortalException, SystemException {

		boolean enabled = isDiscussionsEnabled(userId);

		if (enabled) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		MBMessage message = super.updateDiscussionMessage(
			userId, messageId, className, classPK, subject, body,
			serviceContext);

		AkismetData akismetData = updateAkismetData(message, serviceContext);

		if (!enabled) {
			return message;
		}

		int status = WorkflowConstants.STATUS_APPROVED;

		if (isSpam(userId, subject, body, akismetData)) {
			status = WorkflowConstants.STATUS_DENIED;
		}

		return super.updateStatus(
			userId, message.getMessageId(), status, serviceContext);
	}

	@Override
	public MBMessage updateMessage(
			long userId, long messageId, String subject, String body,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs,
			List<String> existingFiles, double priority, boolean allowPingbacks,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		boolean enabled = isMessageBoardsEnabled(userId);

		if (enabled) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		MBMessage message = super.updateMessage(
			userId, messageId, subject, body, inputStreamOVPs, existingFiles,
			priority, allowPingbacks, serviceContext);

		AkismetData akismetData = updateAkismetData(message, serviceContext);

		if (!enabled) {
			return message;
		}

		int status = WorkflowConstants.STATUS_APPROVED;

		if (isSpam(userId, subject, body, akismetData)) {
			status = WorkflowConstants.STATUS_DENIED;
		}

		return super.updateStatus(
			userId, message.getMessageId(), status, serviceContext);
	}

	protected String getPermalink(
		MBMessage message, ServiceContext serviceContext) {

		StringBundler sb = new StringBundler(4);

		sb.append(serviceContext.getPortalURL());
		sb.append(serviceContext.getPathMain());

		if (message.isDiscussion()) {
			String className = message.getClassName();

			if (className.equals(BlogsEntry.class.getName())) {
				sb.append("/blogs/find_entry?entryId=");
				sb.append(message.getClassPK());
			}
			else if (className.equals(WikiPage.class.getName())) {
				sb.append("/wiki/find_page?pageResourcePrimKey=");
				sb.append(message.getClassPK());
			}
			else {
				return StringPool.BLANK;
			}
		}
		else {
			sb.append("/message_boards/find_entry?messageId=");
			sb.append(message.getMessageId());
		}

		return sb.toString();
	}

	protected boolean isDiscussionsEnabled(long userId)
		throws PortalException, SystemException {

		User user = UserLocalServiceUtil.getUser(userId);

		if (AkismetUtil.isDiscussionsEnabled(user.getCompanyId())) {
			return true;
		}
		else {
			return false;
		}
	}

	protected boolean isMessageBoardsEnabled(long userId)
		throws PortalException, SystemException {

		User user = UserLocalServiceUtil.getUser(userId);

		if (AkismetUtil.isMessageBoardsEnabled(user.getCompanyId())) {
			return true;
		}

		return false;
	}

	protected boolean isSpam(
			long userId, String subject, String body, AkismetData akismetData)
		throws PortalException, SystemException {

		String content = subject + "\n\n" + body;

		User user = UserLocalServiceUtil.getUser(userId);

		if (AkismetUtil.isSpam(
				user.getCompanyId(), akismetData.getUserIP(),
				akismetData.getUserAgent(), akismetData.getReferrer(),
				akismetData.getPermalink(), akismetData.getType(),
				user.getFullName(), user.getEmailAddress(), content)) {

			return true;
		}
		else {
			return false;
		}
	}

	protected AkismetData updateAkismetData(
			MBMessage message, ServiceContext serviceContext)
		throws SystemException {

		String permalink = getPermalink(message, serviceContext);

		Map<String, String> headers = serviceContext.getHeaders();

		String referrer = headers.get("referer");
		String userAgent = headers.get(HttpHeaders.USER_AGENT.toLowerCase());

		String userIP = serviceContext.getRemoteAddr();

		return AkismetDataLocalServiceUtil.updateAkismetData(
			message.getMessageId(), AkismetConstants.TYPE_COMMENT, permalink,
			referrer, userAgent, userIP, StringPool.BLANK);
	}

}