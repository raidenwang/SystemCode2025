<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import { throttle } from "lodash";
import {
	getPrompt,
	getAllChatId,
	getChatContend,
	deleteChat,
	addChat,
	chat,
} from "@/api/chat/chat";
import ChatDialog from "./components/ChatDialog.vue";
import { getAllKnowledgeAPI } from "@/api/knowledge/knowledge";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import EventBusChat from "./utils/eventBus";

const scrollHeight = ref(window.innerHeight - 110);
const chatList = ref([]);
const activeChatIndex = ref(-1);
const knowledges = ref([]);
const selectedKnowledgeArr = ref(["KBa124d786fee0475696806e6319bebdc3"]);
const inputMsg = ref("");
const isAddingChat = ref(false);
const addChatTitle = ref("");
const hasSelectedPlugin = ref(false);
const selectedPluginId = ref(undefined);
const fullscreenLoading = ref(false);
const scrollbarRef = ref(null);
const chatDialogRef = ref(null);
// 保存插件id和对应的名称
const promptsMap = new Map();
//

// 在等待AI回复时，不允许切换chat
let isWaitingResponse = false;
// 保存当前选择的对话下的所有对话
const currentChatWindowDialogList = ref([]);
function onListenerScrollHeight() {
	scrollHeight.value = window.innerHeight - 110;
}
const onListenerScrollHeightThrottle = throttle(onListenerScrollHeight, 500);
onMounted(() => {
	window.addEventListener("resize", onListenerScrollHeightThrottle);
});
onBeforeUnmount(() => {
	window.removeEventListener("resize", onListenerScrollHeightThrottle);
});
// 获取所有左侧的对话记录
function GetAllChatId() {
	getAllChatId().then((res) => {
		chatList.value = res.data;
		// 这个是默认设置为左侧的第一个对话
		// activeChatIndex.value = chatList.value[0]?.id || -1;
	});
}
EventBusChat.on("select-plugin", (pluginId) => {
	if (pluginId) {
		selectedPluginId.value = pluginId;
	}
	hasSelectedPlugin.value = true;
});
getPrompt().then((res) => {
	res.data.forEach((prompt) => {
		promptsMap.set(prompt.id, prompt.name);
	});
});
GetAllChatId();
// 获取所有知识库
getAllKnowledgeAPI().then((res) => {
	knowledges.value = res.data;
});

// 获取当前chatId下的所有历史对话
async function getAllHistoryChatByChatId(chatId) {
	if (isWaitingResponse) {
		ElMessage({
			type: "warning",
			message: "等待问答回复中，请勿切换对话",
		});
		return;
	}
	if (chatId === activeChatIndex.value) return;
	let alreadyReceiveResponse = false;
	let loadingInstance = null;
	setTimeout(() => {
		if (alreadyReceiveResponse) return;
		loadingInstance = startLoading(
			chatDialogRef.value,
			"正在获取该对话历史记录"
		);
	}, 500);
	activeChatIndex.value = chatId;
	try {
		const res = await getChatContend(chatId);
		alreadyReceiveResponse = true;
		currentChatWindowDialogList.value = res.data;
	} finally {
		if (loadingInstance) {
			closeLoading(loadingInstance);
		}
	}
}
// 向AI发送请求
async function sendMsg() {
	// 如果刚进入页面，没有新建对话，直接问答，先创建对话，然后
	// 对话名称就是第一个问题
	if (activeChatIndex.value === -1) {
		addChatTitle.value = inputMsg.value;
		await AddChat();
	}
	const chatInfo = chatList.value?.find(
		(chat) => chat.id === activeChatIndex.value
	);
	const promptId = chatInfo && chatInfo.promptId;
	console.log("promptId", promptId);
	// 收到了就不显示loading了，用来delay
	let alreadyReceiveResponse = false;
	if (inputMsg.value !== "") {
		let loadingInstance = null;
		setTimeout(() => {
			// 已经收到响应了就不用显示loading了
			if (alreadyReceiveResponse) return;
			loadingInstance = startLoading(chatDialogRef.value);
		}, 500);
		try {
			isWaitingResponse = true;
			const res = await chat({
				chatId: activeChatIndex.value,
				// 一个对话只可以用一个插件，默认使用以前选择的插件
				promptId: promptId || selectedPluginId.value,
				content: inputMsg.value,
				kbIds: selectedKnowledgeArr.value,
			});
			alreadyReceiveResponse = true;
			isWaitingResponse = false;
			console.log("获取的res:", res);
			currentChatWindowDialogList.value.push(res.data.question, {
				...res.data.answer,
				chatSourceList: res.data.chatSourceList,
			});
			inputMsg.value = "";
			scroll();
		} finally {
			isWaitingResponse = false;
			if (loadingInstance) {
				closeLoading(loadingInstance);
			}
		}
	}
}

function startLoading(dom, text = "AI正在思考，请等待回复") {
	const loadingInstance = ref(null);
	loadingInstance.value = ElLoading.service({
		lock: true,
		target: dom,
		fullscreen: true,
		text,
	});
	return loadingInstance;
}
function closeLoading(loadingInstance) {
	loadingInstance.value.close();
}
function DeleteChat(chatId) {
	ElMessageBox.confirm("你确认要删除该对话吗", "警告", {
		confirmButtonText: "确认",
		cancelButtonText: "取消",
		type: "warning",
	})
		.then(async () => {
			const res = await deleteChat(chatId);
			if (res.code === 200) {
				GetAllChatId();
				ElMessage({
					type: "success",
					message: "删除成功",
				});
			}
		})
		.catch(() => {
			ElMessage({
				type: "info",
				message: "取消删除",
			});
		});
}
async function AddChat() {
	isAddingChat.value = false;
	if (addChatTitle.value !== "") {
		const newChatId = await addChat(
			addChatTitle.value,
			selectedPluginId.value
		);
		GetAllChatId();
		console.log(newChatId);
		activeChatIndex.value = newChatId.data;
		currentChatWindowDialogList.value = [];
		addChatTitle.value = "";
	}
}
// 采用异步组件解决Teleport挂载的时候DOM不存在的问题
const ChoosePlugin = defineAsyncComponent({
	loader: () => import("./components/ChoosePlugin.vue"),
});
function scroll() {
	nextTick(() => {
		const scrollbar = scrollbarRef.value.wrapRef;
		// console.log(introduce.value);
		if (scrollbar) {
			scrollbar.scrollTop = scrollbar.scrollHeight;
			// console.log(scrollbar.scrollTop + " || " + scrollbar.scrollHeight)
		}
	});
}
function showAllChatTitle(e, chatId) {
	const dom = e.target;
	const spanDom = dom.querySelector(".one-chat-text");
	spanDom.classList.add("show-all-text");
	// const { chatName } = chatList.value.find((chat) => chat.id === chatId);
	// spanDom.textContent = chatName;
}
function hideAllChatTitle(e, chatId) {
	if (activeChatIndex.value === chatId) return;
	const dom = e.target;
	const spanDom = dom.querySelector(".one-chat-text");
	spanDom.classList.remove("show-all-text");
}
</script>

<template>
	<div class="container inserted">
		<Transition name="fade">
			<ChoosePlugin v-if="!hasSelectedPlugin" />
		</Transition>
		<div
			class="open-choose-plugin-compoent-btn"
			@click="hasSelectedPlugin = false"
		>
			重新选择插件
		</div>
		<el-row>
			<el-col :span="7" class="left-chat-list">
				<Fragment>
					<el-button
						type="primary"
						class="add-chat-btn"
						plain
						v-if="!isAddingChat"
						@click="isAddingChat = true"
						>新建对话 (
						{{ promptsMap.get(selectedPluginId) }} )</el-button
					>
					<el-input
						v-model="addChatTitle"
						placeholder="请输入对话标题，回车继续"
						class="add-chat-btn"
						@keyup.enter="() => AddChat()"
						v-else
					/>
				</Fragment>
				<el-scrollbar :height="scrollHeight" class="chat-list-scroll">
					<!-- 已存在的对话 -->
					<div
						v-for="item in chatList"
						:key="item.id"
						class="one-chat"
						:class="{ active: item.id == activeChatIndex }"
						@click="() => getAllHistoryChatByChatId(item.id)"
						@mouseenter="(e) => showAllChatTitle(e, item.id)"
						@mouseleave="(e) => hideAllChatTitle(e, item.id)"
					>
						<span
							class="one-chat-text"
							:class="{
								'show-all-text': item.id == activeChatIndex,
							}"
							>{{ item.chatName }}</span
						>
						<!-- 接口修复后，改为这个 -->
						<span
							class="one-chat-plugin-name"
							v-if="promptsMap.has(item.promptId)"
							:id="item.id"
							>(
							{{ promptsMap.get(item.promptId) }}
							)</span
						>
						<Fragment
							v-if="activeChatIndex == item.id"
							class="chat-delete-icon"
							@click="() => DeleteChat(item.id)"
						>
							<el-icon><Delete /></el-icon>
						</Fragment>
					</div>
				</el-scrollbar>
			</el-col>
			<el-col :span="17" class="right-chat-window">
				<Fragment ref="chatDialogRef">
					<el-scrollbar
						:height="scrollHeight - 140"
						class="chat-window-scroll"
						ref="scrollbarRef"
					>
						<ChatDialog :propsArray="currentChatWindowDialogList" />
					</el-scrollbar>
				</Fragment>
				<div class="select-box">
					<el-select
						v-model="selectedKnowledgeArr"
						placeholder="请选择知识库"
						size="large"
						multiple
						collapse-tags
						style="width: 240px"
					>
						<el-option
							v-for="item in knowledges"
							:key="item.id"
							:label="item.name"
							:value="item.kbId"
						/>
					</el-select>
				</div>
				<div class="send-msg-box">
					<el-input
						v-model="inputMsg"
						placeholder="请输入内容"
						class="send-msg-inp"
						@keyup.enter="sendMsg"
					/>
					<el-button
						type="primary"
						class="send-msg-btn"
						@click="sendMsg()"
						>发送</el-button
					>
				</div>
			</el-col>
		</el-row>
	</div>
</template>

<style lang="scss" scoped>
.container {
	.open-choose-plugin-compoent-btn {
		position: absolute;
		display: flex;
		align-items: center;
		justify-content: center;
		left: calc(50% + 25px);
		top: 50px;
		width: 40%;
		height: 30px;
		background-color: #409eff;
		padding: 4px 4px 4px 0;
		color: #fff;
		transform: translateX(-50%);
		z-index: 11;
		cursor: pointer;
		&::after {
			position: absolute;
			left: 0;
			width: 0;
			height: 0;
			content: "";
			border-right: 0;
			border-left: 30px solid #fff;
			border-top: 30px solid #409eff;
			border-bottom: 0;
		}
		&::before {
			position: absolute;
			right: 0;
			width: 0;
			height: 0;
			content: "";
			border-right: 30px solid #fff;
			border-left: 0;
			border-top: 30px solid #409eff;
			border-bottom: 0;
		}
	}
	.left-chat-list {
		position: relative;
		background-color: #eef1f6;
		.add-chat-btn {
			position: relative;
			height: 40px;
			width: 90%;
			margin: 10px 0;
			left: 50%;
			transform: translateX(-50%);
		}
		.chat-list-scroll {
			// background-color: blue;
			.one-chat {
				display: flex;
				align-items: center;
				position: relative;
				width: 90%;
				height: 50px;
				margin: 10px auto;
				cursor: pointer;
				padding: 6px;
				min-height: 50px;
				height: auto;
				.one-chat-text {
					// width: 80%;
					flex: 4;
					white-space: nowrap;
					// background-color: red;
					text-overflow: ellipsis;
					overflow: hidden;
					margin-right: 20px;
					font-size: 18px;
					&.show-all-text {
						text-overflow: clip;
						overflow: visible;
						white-space: wrap;
						font-size: 18px;
						height: auto;
					}
				}
				.one-chat-plugin-name {
					// width: 40%;
					flex: 3;
					margin-right: 20px;
					font-size: 14px;
				}
				&.active {
					background-color: #d3def6;
					border-radius: 10px;
				}
				&:hover:not(.active) {
					background-color: #ebf5ff;
					border-radius: 10px;
				}
				.chat-delete-icon {
					position: absolute;
					right: 10px;
					top: 50%;
					transform: translateY(-50%);
					z-index: 10;
				}
			}
		}
	}
	.right-chat-window {
		position: relative;
		padding: 0 40px;
		.chat-window-scroll {
			padding-top: 40px;
			// background-color: red;
		}
		.select-box {
			position: absolute;
			bottom: 100px;
			left: 40px;
		}
		.send-msg-box {
			display: flex;
			align-items: center;
			position: absolute;
			width: calc(100% - 100px);
			height: 50px;
			bottom: 30px;
			left: 40px;
			.send-msg-inp {
				height: 50px;
				flex: 8;
				width: calc(100% - 300px);
			}
			.send-msg-btn {
				flex: 2;
				margin-left: 20px;
				height: 100%;
			}
		}
	}
}
</style>
