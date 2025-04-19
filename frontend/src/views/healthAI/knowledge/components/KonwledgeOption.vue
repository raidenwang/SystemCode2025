<script setup>
import { ref, watch } from 'vue';
import EventBus from '../utils/eventBus';
const props = defineProps({
    width: {
        required: false,
        type: String,
        defalut: '100%'
    },
    // height:{
    //     required:false,
    //     type:String,
    //     defalut:'50px'
    // }
    name: {
        required: false,
        type: String,
        defalut: '默认知识库'
    },
    id: {
        required: true,
        type: String
    },
    // 0公开，1私有
    type: {
        required: true,
        type: Number
    },
    isActive: {
        required: false,
        type: Boolean,
        defalut: false
    },
    creator:{
        required:true,
        type:String,
    },
    currentUser:{
        required:true,
        type:Object
    }
})
const isRename = ref(false)
// 存储持久使用的名字
const knowledgeName = ref(props.name)
// 存储输入的名字
const inpNameValue = ref(props.name)
function renameKnowledge() {
    if (inpNameValue.value === '') {
        isRename.value = false
        return
    }
    knowledgeName.value = inpNameValue.value
    // 事件总线通知外层，可能要修改右侧知识库的title
    EventBus.emit('renameKnowledge', { id: props.id, newName: knowledgeName.value, type: props.type })
    isRename.value = false
}
// 调用接口删除这个知识库
function removeKnowledge() {
    EventBus.emit('removeKnowledge', { id: props.id })
    console.log('传递的id是', props.id)
}
function switchType(){
    EventBus.emit('switchType',{id: props.id, name: knowledgeName.value, type: props.type === 1 ? 0 : 1})
}
// 只有创建者或者超级管理员才可以展示操作
function verifyIsCanModify(){
    // console.log(props.currentUser.userName,props.currentUser.roleKey)
    if(props.creator === props.currentUser.userName || props.currentUser.roleKey === 'admin'){
        return true
    }
    return false
}
</script>


<template>
    <div class="container" :style="{
        width: props.width,
        backgroundColor: props.isActive ? 'rgb(108,143,233)' : 'rgb(235,237,248)',
    }"
     :data-id="props.id">
        <el-tag :type="props.type === 1 ? 'danger' : 'success'" class="knowledge-type" v-if="!isRename">
            {{ props.type === 1 ? '私有' : '公开' }}
        </el-tag>
        <el-popover placement="right" trigger="hover" hide-after="120" show-after="120" :popper-style="{
            minWidth: '145px'
        }" :disabled="isRename || !verifyIsCanModify()" offset="60">
            <template #reference>

                <div class="rename-box" v-if="isRename">
                    <el-input v-model="inpNameValue" placeholder="请输入知识库名字"></el-input>
                    <el-icon class="confirm" @click="renameKnowledge" size="26">
                        <Check />
                    </el-icon>
                    <el-icon class="cancel" @click="() => isRename = false" size="26">
                        <Close />
                    </el-icon>
                </div>
                <div class="knowledge-name" v-else>
                    <span>{{ knowledgeName }}</span>
                </div>
            </template>
            <template #default>
                <div class="operate-content" :style="{
                    display: 'flex',
                    flexDirection: 'column',
                }">
                    <div class="rename" :style="{
                        fontSize: '16px',
                        marginBottom: '10px',
                        cursor: 'pointer',
                        userSelect:'none'
                    }" @click="() => {isRename = true
                    inpNameValue = knowledgeName}">
                        <el-icon><Document /></el-icon><span :style="{margin: '0 10px'}">重命名</span>
                    </div>
                    <div class="switch-type" :style="{fontSize: '16px',marginBottom: '10px',}">
                        <el-icon><Switch /></el-icon>
                        <span :style="{margin: '0 10px',cursor: 'pointer'}" @click="switchType">切换为{{ props.type === 1 ? '公开':'私有' }}</span>
                    </div>
                    <div class="delete" :style="{fontSize: '16px'}">
                        <el-icon><Delete /></el-icon>
                        <span :style="{margin: '0 10px',cursor: 'pointer'}" @click="removeKnowledge">删除</span>
                    </div>
                </div>
            </template>
        </el-popover>
    </div>
</template>


<style scoped lang="scss">
.container {
    position: relative;
    margin: 0;
    width: 100%;
    height: 100px !important;
    border-radius: 10px;
    // background-color: #79bbff;
    &:hover {
        background-color: rbg(215, 222, 242);
    }

    .knowledge-type {
        position: absolute;
        right: 14px;
        top: 5px;
        width: 36px;
        height: 24px;
        border-radius: 6px;
        pointer-events: none;
    }

    .rename-box {
        position: absolute;
        top: 15px;
        left: 10px;
        width: 60%;

        .confirm {
            position: absolute;
            top: 4px;
            right: -45px;
            cursor: pointer;
        }

        .cancel {
            position: absolute;
            top: 4px;
            cursor: pointer;
            right: -80px;
        }
    }

    .knowledge-name {
        width: 80%;
        padding: 10px;

        span {
            display: inline-block;
            width: 100%;
            height: 100%;
        }
    }
}
</style>