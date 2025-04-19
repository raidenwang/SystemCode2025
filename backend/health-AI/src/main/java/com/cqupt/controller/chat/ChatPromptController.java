package com.cqupt.controller.chat;

import com.cqupt.common.annotation.Log;
import com.cqupt.common.core.controller.BaseController;
import com.cqupt.common.core.domain.AjaxResult;
import com.cqupt.common.core.page.TableDataInfo;
import com.cqupt.common.enums.BusinessType;
import com.cqupt.common.utils.poi.ExcelUtil;
import com.cqupt.domain.chat.ChatPrompt;
import com.cqupt.service.chat.IChatPromptService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 提示词Controller
 * 
 * @author ruoyi
 * @date 2024-06-29
 */
@RestController
@RequestMapping("/system/prompt")
@Api(tags = "提示词")
public class ChatPromptController extends BaseController
{
    @Autowired
    private IChatPromptService chatPromptService;

    /**
     * 查询提示词列表
     */
    @PreAuthorize("@ss.hasPermi('system:prompt:list')")
    @GetMapping("/list")
    public TableDataInfo list(ChatPrompt chatPrompt)
    {
        startPage();
        List<ChatPrompt> list = chatPromptService.selectChatPromptList(chatPrompt);
        return getDataTable(list);
    }

    /**
     * 导出提示词列表
     */
    @PreAuthorize("@ss.hasPermi('system:prompt:export')")
    @Log(title = "提示词", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChatPrompt chatPrompt)
    {
        List<ChatPrompt> list = chatPromptService.selectChatPromptList(chatPrompt);
        ExcelUtil<ChatPrompt> util = new ExcelUtil<ChatPrompt>(ChatPrompt.class);
        util.exportExcel(response, list, "提示词数据");
    }

    /**
     * 获取提示词详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:prompt:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(chatPromptService.selectChatPromptById(id));
    }

    /**
     * 新增提示词
     */
    @PreAuthorize("@ss.hasPermi('system:prompt:add')")
    @Log(title = "提示词", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ChatPrompt chatPrompt)
    {
        return toAjax(chatPromptService.insertChatPrompt(chatPrompt));
    }

    /**
     * 修改提示词
     */
    @PreAuthorize("@ss.hasPermi('system:prompt:edit')")
    @Log(title = "提示词", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ChatPrompt chatPrompt)
    {
        return toAjax(chatPromptService.updateChatPrompt(chatPrompt));
    }

    /**
     * 删除提示词
     */
    @PreAuthorize("@ss.hasPermi('system:prompt:remove')")
    @Log(title = "提示词", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(chatPromptService.deleteChatPromptByIds(ids));
    }
}
