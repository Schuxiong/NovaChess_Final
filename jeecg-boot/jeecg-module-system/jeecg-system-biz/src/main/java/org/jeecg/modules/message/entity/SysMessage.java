package org.jeecg.modules.message.entity;

import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.system.base.entity.JeecgEntity;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_message")
public class SysMessage extends JeecgEntity {
	/**发送者头像*/
	@Excel(name = "发送者头像", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_sender_avatar")
	private java.lang.String esSenderAvatar;
	
	/**接收者头像*/
	@Excel(name = "接收者头像", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_receiver_avatar")
	private java.lang.String esReceiverAvatar;
	/**推送内容*/
	@Excel(name = "推送内容", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_content")
	private java.lang.String esContent;
	/**推送所需参数Json格式*/
	@Excel(name = "推送所需参数Json格式", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_param")
	private java.lang.String esParam;
	/**接收人*/
	@Excel(name = "接收人", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField(exist = false)
	private java.lang.String esReceiver;
	/**推送失败原因*/
	@Excel(name = "推送失败原因", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_result")
	private java.lang.String esResult;
	/**发送次数*/
	@Excel(name = "发送次数", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_send_num")
	private java.lang.Integer esSendNum;
	/**推送状态 0未推送 1推送成功 2推送失败*/
	@Excel(name = "推送状态 0未推送 1推送成功 2推送失败", width = 15)
	@Dict(dicCode = "msgSendStatus")
	@com.baomidou.mybatisplus.annotation.TableField("es_send_status")
	private java.lang.String esSendStatus;
	/**推送时间*/
	@Excel(name = "推送时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@com.baomidou.mybatisplus.annotation.TableField("es_send_time")
	private java.util.Date esSendTime;
	/**消息标题*/
	@Excel(name = "消息标题", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_title")
	private java.lang.String esTitle;
	/**
	 * 推送方式：参考枚举类MessageTypeEnum
	 */
	@Excel(name = "推送方式", width = 15)
	@Dict(dicCode = "messageType")
	@com.baomidou.mybatisplus.annotation.TableField("es_type")
	private java.lang.String esType;
	/**备注*/
	@Excel(name = "备注", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("remark")
	private java.lang.String remark;
	
	/**发送者ID*/
	@Excel(name = "发送者ID", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_sender_id")
	private java.lang.String esSenderId;
	/**发送者名称*/
	@Excel(name = "发送者名称", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_sender_name")
	private java.lang.String esSenderName;
	/**接收者ID*/
	@Excel(name = "接收者ID", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_receiver_id")
	private java.lang.String esReceiverId;
	/**接收者名称*/
	@Excel(name = "接收者名称", width = 15)
	@com.baomidou.mybatisplus.annotation.TableField("es_receiver_name")
	private java.lang.String esReceiverName;
	/**消息类型 0:系统消息 1:聊天消息*/
	@Excel(name = "消息类型", width = 15)
	@Dict(dicCode = "messageCategory")
	@com.baomidou.mybatisplus.annotation.TableField("es_category")
	private java.lang.String esCategory;
	/**阅读状态(0:未读,1:已读)*/
	@Excel(name = "阅读状态", width = 15)
	@Dict(dicCode = "readStatus")
	@com.baomidou.mybatisplus.annotation.TableField("es_read_status")
	private java.lang.String esReadStatus;
}
