/** Created by flym at 11/13/2014 */
package com.ecnu.trivia.web.common.exception;


import com.ecnu.trivia.common.component.exception.ExceptionMessage;
import com.ecnu.trivia.common.component.exception.ExceptionMessageValue;

/**
 * 定义成员组织功能的错误信息
 *
 * @author flym
 */
public interface SystemExceptionMessageValue extends ExceptionMessageValue
{

    ExceptionMessage DUPLICATE_PROJECT_4_CREATE = new ExceptionMessage("project.duplicate.create", "不可添加,项目 {} 名称已存在");
    ExceptionMessage DUPLICATE_PROJECT_4_UPDATE = new ExceptionMessage("project.duplicate.updateTag", "项目名称已存在，请重新输入");
    ExceptionMessage DUPLICATE_TAG_CAPTION_CREATE = new ExceptionMessage("duplicate.tag.caption.create", "不可添加,小组 {} 名称已存在");
    ExceptionMessage DUPLICATE_TAG_CAPTION_UPDATE = new ExceptionMessage("duplicate.tag.caption.updateTag", "不可修改,小组 {} 名称已存在");
    ExceptionMessage NOT_ORG_MEMBER = new ExceptionMessage("user.not.org.member", "不可切换,用户{}不是组织{}成员");
    ExceptionMessage CAPTION_IS_NULL = new ExceptionMessage("caption.is.null", "请填写项目名称");
    ExceptionMessage ACCESS_LEVEL_IS_NULL = new ExceptionMessage("accesslevel.is.null", "请填写项目可见性");
    ExceptionMessage DUPLICATE_JOIN_APPLICATION = new ExceptionMessage("duplicate.join.application", "不可添加重复申请，组织{}中用户{}已存在");
    ExceptionMessage ORGANIZATION_NO_EXIST = new ExceptionMessage("organization.no.exist", "组织不存在");
    ExceptionMessage OPEN_LINK_INVALID = new ExceptionMessage("open.link.invalid", "链接已失效");
    ExceptionMessage REQUEST_NOT_MATCH = new ExceptionMessage("request.not.match", "请求加入的组织{}，与当前组织{}不匹配");
    ExceptionMessage PROJECT_FIELD_NO_EXIST = new ExceptionMessage("project_field_no_exist", "不存在编号为{}的项目字段");
    ExceptionMessage TABLE_SOURCE_NOT_EXIST = new ExceptionMessage("table.source.not.exist", "数据源类型不存在");

    ExceptionMessage PROJECT_CUBE_APPLICATION_NOT_APPLIED = new ExceptionMessage("projectCubeApplication.notApplied", "相应的数据主题并不处于申请状态:{}");
    ExceptionMessage PROJECT_CUBE_APPLICATION_AUTHORIZED = new ExceptionMessage("projectCubeApplication.authorized", "相应的数据主题已分配给此项目");

    ExceptionMessage ORG_NOT_EQUALS_BIND = new ExceptionMessage("org.operate.noteq.bind", "所操作的组织 {} 与当前组织 {} 不一致");
    ExceptionMessage ORG_NOT_ACCESS_CUBE = new ExceptionMessage("org.operate.cube.notauth", "当前组织{}，不可访问此数据主题{}");
    ExceptionMessage ORG_NOT_ACCESS_PROJECT = new ExceptionMessage("org.operate.project.notauth", "当前组织 {} 不能访问此项目 {}");
    ExceptionMessage ORG_ROLE_RESTRICTED = new ExceptionMessage("org.role.restricted", "分配的组织角色必须为管理员或成员:{}");
    ExceptionMessage ORG_ROLE_USER_RESTRICTED = new ExceptionMessage("org.role.user.restricted", "不可给超级管理员分配组织角色:{}");

    ExceptionMessage PROJECT_NOT_ACCESS_CUBE = new ExceptionMessage("project.operate.cube.notauth", "当前项目 {} 不可访问此数据主题 {}");
    ExceptionMessage PROJECT_NOT_BELONG_ORG = new ExceptionMessage("project.relation.org.notmatch", "当前项目 {} 不属于当前组织 {}");
    ExceptionMessage PROJECT_ROLE_RESTRICTED = new ExceptionMessage("project.role.restricted", "分配角色必须是相应项目角色:{}");
    ExceptionMessage PROJECT_NOT_EXISTS = new ExceptionMessage("project.not.exists", "指定项目不存在:{}");
    ExceptionMessage PROJECT_NOT_ACCESS = new ExceptionMessage("project.notauth", "当前不可访问此项目:{}");
    ExceptionMessage PROJECT_USER_NOT_ACCESS_CUBE = new ExceptionMessage("project.user.operate.cube.notauth", "用户 {} 不可访问项目 {} 以及相应数据主题 {} ");

    ExceptionMessage PERMISSION_FORBID = new ExceptionMessage("permission.forbid", "当前权限不可操作:{}");
}
