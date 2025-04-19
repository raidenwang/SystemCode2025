import { ElMessage,ElTag ,ElScrollbar,ElLoading } from 'element-plus';
import {throttle} from 'lodash'
// import { onBeforeMount } from 'vue';
import { onBeforeUnmount , h} from 'vue';
import useUserStore from '../../../../store/modules/user';
export function getTagStatusColor(status) {
    if (status === 'green') return 'success'
    else if (status === 'gray') return 'warning'
    else {
        return 'danger'
    }
}

export function getTagStatusText(status) {
    if (status === 'green') return '上传成功'
    else if (status === 'gray') return '正在上传'
    else {
        return '上传失败'
    }
}

export function validateUploadFileType(filename) {
    const supportedTypes = ['md', 'txt', 'pdf', 'jpg', 'png', 'jpeg', 'docx', 'xlsx', 'pptx', 'eml', 'csv']
    // console.log('后缀名:',filename.split('.').pop().toLowerCase())
    return supportedTypes.includes(filename.split('.').pop().toLowerCase())
}

// 校验是否输入了机构名和发行日期
export function validate(inpOrganizationValue,inpDataPickerValue) {
    if (inpOrganizationValue === '' || inpDataPickerValue === '') {
        ElMessage({
            type: 'warning',
            message: '请输入完整信息'
        })
        return false
    }
    return true
}

// export function useOnListenToChangeVirtualTableSize(){

//     // 1.进入页面获取宽和高
//     const windowHeight = window.innerHeight
//     const windowWidth = window.innerWidth
//     // 2.按需要的比例赋值给宽和高的ref值
//     const virtualTableHeight = ref(windowHeight)
//     const virtualTableWidth = ref(windowWidth)
//     // 3.监听resize，加一个节流
//     windowAddEventListener('resize',)
//     // 4.返回使用
    
//     return [virtualTableWidth,virtualTableWidth]
// }


// function windowAddResizeEventListener(type,func,params){
//     const finallFunc = func(...params)
//     window.addEventListener(type,finallFunc)
//     onBeforeUnmount(()=>window.removeEventListener(type,finallFunc))
// }

// function setVirtualTableSize(){

// }
export function virtualTableTagShowing(cellData){
    if(isObjEmpty(cellData)) return '无'
    // console.log(cellData)
    let vnodeElTagArr = []
    // 样式
    const style = {
        display:'flex',
        justifyContent:'center',
        alignItems:'center',
        margin:'6px',
        fontSize:'14px'
    }
    // key是标签，val是标签的值
    for(let key in cellData){
        vnodeElTagArr.push(h(
            ElTag,
            {key,style,type:'warning',size:'large'},
            {default:()=> `${key} : ${cellData[key]}`} 
        ))
    }
    // 返回el-tag的虚拟节点
    return h(
        ElScrollbar,
        {maxHeight:'100px',overflow:'visible',onwheel:e=>{
            let dom = e.target
            while(dom && dom.parentElement && ![...dom.classList].includes('el-scrollbar__view')){
                dom = dom.parentElement
            }
            if(dom && [...dom.classList].includes('el-scrollbar__view')){
                e.stopPropagation()
            }
        }},
        vnodeElTagArr
    )
}


function isObjEmpty(obj){
    return obj == null || JSON.stringify(obj) == "{}"
}

// 传递dom，dom元素开启loading
export function startLoading(dom,text = '正在加载中'){
    const loadingInstance = ref(null)
    loadingInstance.value = ElLoading.service({
        target:dom, // 让loading展示在该dom上
        fullscreen:false,
        text,
    })
    return loadingInstance
}

export function closeLoading(loadingInstance){
    loadingInstance.value.close()
}

/**
 * 全部文件状态确定了就返回true，不用轮询了，减少轮询次数
 * @param {Array} docList 表格列表文件数组
 */
export function isAllFileStatusConfirmed(docList){
    const len = docList.length
    for(let i=0;i<length;++i){
        if(docList[i].status === 'gray'){
            return false
        }
    }
    return true
}