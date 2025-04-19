package com.cqupt.domain.chat;

import com.cqupt.common.annotation.Excel;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 提示词对象 chat_prompt
 * 
 * @author
 * @date 2024-06-29
 */
@Data
public class ChatPrompt
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 提示词名称 */
    @Excel(name = "提示词名称")
    private String name;

    /** 提示词内容 */
    @Excel(name = "提示词内容")
    private String content;

    private String introduce;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("content", getContent())
            .toString();
    }
}
