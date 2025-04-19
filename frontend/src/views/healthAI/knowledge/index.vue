<template>
	<div class="container">
		<div class="knowledge-options" ref="knowledgeOptionsRef">
			<div class="add-knowledge">
				<div class="title-and-add-btn">
					<span class="left-title">知识库</span>
					<el-button
						:icon="Plus"
						circle
						color="#d7def2"
						class="add-knowledge-btn"
						@click="addKnowledgeDialogVisible = true"
					></el-button>
				</div>
				<el-dialog
					v-model="addKnowledgeDialogVisible"
					class="add-knowledge-dialog"
					title="新增知识库"
				>
					<el-input
						class="add-knowledge-inp"
						placeholder="请输入知识库名称"
						v-model="addKnowledgeInpValue"
					/>
					<el-select
						v-model="addKnowledgeTypeValue"
						placeholder="请选择知识库类型"
						class="add-knowledge-type-selector"
					>
						<el-option
							v-for="item in knowledgeTypeOptions"
							:key="item.value"
							:label="item.label"
							:value="item.value"
							:disabled="
								currentUser.roleKey === 'user' &&
								item.label === '公开'
							"
						/>
					</el-select>
					<el-button class="add-knowledge-btn" @click="addKnowledge"
						>新增</el-button
					>
				</el-dialog>
			</div>
			<el-scrollbar
				:max-height="knowledgeOptionsScrollHeight + 'px'"
				class="scroll"
			>
				<div
					class="knowledge-option"
					v-for="(item) in knowledgeArr"
					:key="item.id"
				>
					<KonwledgeOption
						:name="item.name"
						:id="item.id"
						:type="item.type"
						@click="(e) => changeKnowledgeDocument(e)"
						:isActive="activeKnowledgeId == item.id"
						:creator="item.creator"
						:currentUser="currentUser"
						class="knowledge-option-hover"
						@mouseover="changeBgMouseOver"
						@mouseleave="changeBgMouseLeave"
					/>
				</div>
			</el-scrollbar>
		</div>
		<div class="knowledge-table">
			<div class="knowledge-title">
				<span>{{ knowledgeTitle }}</span>
			</div>
			<!-- :on-success="UploadFileSuccessfully" -->
			<!-- :on-progress="UploadFile" -->
			<el-button
				class="upload-document-btn"
				size="large"
				@click="() => (upLoadDialogVisible = true)"
				:disabled="!isCanModify"
				>上传文档</el-button
			>
			<!-- 弹出的上传文档弹窗  -->
			<el-dialog
				v-model="upLoadDialogVisible"
				class="upload-document-dialog"
				:closeOnClickModal="false"
				:show-close="false"
			>
				<div class="upload-card">
					<div class="upload-card-column-1">
						<el-input
							v-model="inpOrganizationValue"
							style="width: 240px"
							placeholder="请输入机构"
							class="upload-organization"
							:prefix-icon="Search"
						/>
						<el-date-picker
							v-model="inpDataPickerValue"
							type="date"
							class="data-picker"
							placeholder="请输入日期"
							value-format="YYYY-MM-DD"
						/>
						<el-upload
							class="upload-document-region"
							multiple
							action="http://10.16.53.4:8080/knowledgeBase/uploadFiles"
							:show-file-list="false"
							:file-list="uploadFileList"
							:http-request="UploadFileHTTP"
							:auto-upload="false"
							:on-success="UploadFileSuccessfully"
							:on-error="UploadFileError"
							:on-change="selectLocalFiles"
							ref="uploadRef"
							:before-upload="beforeUploadFileCheck"
							:accept="'.md, .txt, .jpg, .png, .jpeg, .docx, .xlsx, .pptx, .eml, .csv, .pdf'"
						>
							<el-button class="upload-document-region-btn"
								><el-icon><upload-filled /></el-icon
								>上传文件</el-button
							>
						</el-upload>
						<!-- <el-button class="upload-add-btn" @click="addFile" type="primary" plain>添加文件</el-button> -->
					</div>
					<div class="upload-card-column-2">
						<el-input
							v-model="tagNameValue"
							placeholder="请输入标签名称"
							class="tag-name"
							:prefix-icon="Discount"
						/>
						<el-input
							v-model="tagContentValue"
							placeholder="请输入标签内容"
							class="tag-content"
							:prefix-icon="EditPen"
						/>
						<el-button class="add-label-btn" @click="addLabel"
							>添加标签</el-button
						>
					</div>
					<div class="upload-card-column-3">
						<el-button
							class="upload-add-btn"
							@click="addFile"
							type="primary"
							plain
							>添加文件</el-button
						>
					</div>
				</div>
				<!-- 这里存放已经指明的标签的key value -->
				<span v-if="tagArr.length >= 1" :style="{ marginRight: '10px' }"
					>已添加标签</span
				>
				<div
					v-for="(item) in tagArr"
					:key="item.tagName"
					class="added-label"
				>
					<el-tag
						type="warning"
						closable
						@close="deleteLabel(item.tagName)"
						>{{ item.tagName }} : {{ item.tagContent }}</el-tag
					>
				</div>
				<div class="chosen-files-box">
					<el-scrollbar max-height="200px">
						<div
							class="chosen-file"
							v-for="(item) in chosenFiles"
							:key="item.name"
						>
							<el-link class="chosen-file-name">{{
								item.name
							}}</el-link>
							<!-- <el-progress :percentage="50" class="chosen-file-progress" /> -->
							<span
								class="chosen-file-del-btn"
								@click="deleteChosenFile(item.name)"
								>删除</span
							>
						</div>
					</el-scrollbar>
				</div>
				<!-- 这里放上传的文件展示列表 -->
				<div class="uploaded-file-list">
					<el-table
						:data="
							uploadFileList.map((item) => ({
								name: item.file.name,
								organization: item.organization,
								date: item.date,
								tag: item.tag,
							}))
						"
						max-height="500"
						:current-row-key="UploadTabelCurrentRow"
						@row-click="UploadTabelOnClick"
					>
						<el-table-column
							prop="name"
							label="文件名"
							min-width="20%"
						/>
						<el-table-column
							prop="organization"
							label="机构"
							min-width="20%"
						/>
						<el-table-column
							prop="date"
							label="发行日期"
							min-width="20%"
						/>
						<el-table-column
							prop="tag"
							label="标签"
							min-width="20%"
						>
							<template #default="{ row }">
								<el-scrollbar>
									<el-tag
										v-for="item in row.tag"
										:key="item.tagName"
										:style="{
											display: 'flex',
											margin: '4px',
										}"
										type="warning"
									>
										{{ item.tagName }} :
										{{ item.tagContent }}
									</el-tag>
								</el-scrollbar>
							</template>
						</el-table-column>
						<el-table-column
							label="操作"
							fixed="right"
							min-width="20%"
						>
							<template #default>
								<!-- <el-button type="primary">查看</el-button> -->
								<span
									:style="{
										color: 'red',
										cursor: 'pointer',
										userSelect: 'none',
									}"
									>删除</span
								>
							</template>
						</el-table-column>
					</el-table>
				</div>
				<el-button @click="CancelUploadAllFile" class="upload-cancel"
					>取消上传</el-button
				>
				<el-button
					@click="UploadAllFile"
					class="upload-confirm"
					type="primary"
					>全部上传</el-button
				>
			</el-dialog>
			<!--  -->
			<div class="document-table" ref="virtualTable">
				<!-- :data="documentDocList" -->
				<!-- @click="deleteKnowledgeOnlineFile" -->
				<el-auto-resizer>
					<el-table-v2
						:columns="columns"
						:data="documentDocList"
						:width="virtualTableWidth"
						:height="virtualTableHeight"
						headerClass="virtual-table-header"
						:fixed="false"
						:cache="10"
						:row-height="120"
					/>
				</el-auto-resizer>
			</div>
			<!--  -->
		</div>
	</div>
</template>

<script setup>
import { throttle } from "lodash";
import { Search, Plus, Discount, EditPen } from "@element-plus/icons-vue";
import "element-plus/es/components/message/style/css";
import { ElMessage, ElLoading} from "element-plus";
import { ElTag,ElButton,ElMessageBox } from "element-plus";
import request from "@/utils/request";
import { onMounted, ref, h } from "vue";
import KonwledgeOption from "./components/KonwledgeOption.vue";
import EventBus from "./utils/eventBus";
import {
	addNewKnowledgeAPI,
	deleteKnowledgeFileAPI,
	getAllFilesWithStatusAPI,
	getAllKnowledgeAPI,
	getKnowledgeAllFileStatusAPI,
	getOneKnowledgeByIdAPI,
	pollKnowledgeAllFilesStatusAPI,
	removeKnowledgeByIdAPI,
	updateKnowledgeAPI,
	uploadKnowledgeFileAPI,
} from "../../../api/knowledge/knowledge";
import { closePollById, closePollExcept } from "./utils/poll";
import {
	getTagStatusColor,
	getTagStatusText,
	validateUploadFileType,
	validate,
	virtualTableTagShowing,
	startLoading,
	closeLoading,
	isAllFileStatusConfirmed,
} from "./utils/utils";
import useUserStore from "../../../store/modules/user";

const virtualTable = ref(null);
const isCanModify = ref(false);
const currentUser = ref("");
// 存放输入的多对标签
const tagArr = ref([]);
const tagContentValue = ref("");
const tagNameValue = ref("");
const addKnowledgeDialogVisible = ref(false);
// 左侧的知识库标签栏目，获取它的宽度给右侧表格参考
const knowledgeOptionsRef = ref(null);
const knowledgeOptionsScrollHeight = ref(0);
const virtualTableHeight = ref(0);
const virtualTableWidth = ref(0);
// 保证提醒只出现一次
let hasElMessage = false;
// 存储一下正在执行删除的知识库id，避免多次删除同一个返回500
let isDelKnowledgeIdArr = [];
const addKnowledgeTypeValue = ref(null);
const knowledgeTypeOptions = [
	{
		label: "私有",
		value: 1,
	},
	{
		label: "公开",
		value: 0,
	},
];
// 用于存储展示的右侧数据
const documentDocList = ref([]);
// 当前行的row，用于删除
const UploadTabelCurrentRow = ref(0);
// 存储当前选择的文件
const chosenFiles = ref([]);
const uploadRef = ref(null);
const inpDataPickerValue = ref("");
const inpOrganizationValue = ref("");
//上传数据用的fileList
const uploadFileList = ref([]);
// 用于传递给table显示数据
const konwledgeDocument = ref([]);
const upLoadDialogVisible = ref(false);
const addKnowledgeInpValue = ref("");
// 所有的知识库
const knowledgeArr = ref([]);
// 当前活跃的知识库
const activeKnowledgeId = ref(
	(knowledgeArr.value[0] && knowledgeArr.value[0].id) || 0
);
const knowledgeTitle = ref(
	(knowledgeArr.value[0] && knowledgeArr.value[0].name) || "默认知识库"
);
// key: 上传文件的id  val:对应的tag对象
const tagMap = new Map();
//
let columns = [
	{
		key: "id",
		dataKey: "id",
		title: "ID",
		width: "10%",
		align: "center",
	},
	{
		key: "name",
		dataKey: "name",
		title: "名称",
		width: "25%",
		align: "center",
	},
	{
		key: "status",
		dataKey: "status",
		title: "状态",
		width: "10%",
		// width: '100',
		align: "center",
		// cellData就是里面的文本内容
		
		cellRenderer: ({ cellData }) =>
			h(
				ElTag,
				{
					type: getTagStatusColor(cellData),
					size: "large",
					disableTransitions: true,
				},
				{ default: () => getTagStatusText(cellData) }
			),
	},
	{
		key: "institution",
		dataKey: "institution",
		title: "机构",
		width: "18%",
		align: "center",
	},
	{
		key: "date",
		dataKey: "date",
		title: "发行日期",
		// width: '220',
		width: "10%",
		align: "center",
	},
	{
		key: "tag",
		dataKey: "tag",
		title: "标签",
		// width: '220',
		width: "22%",
		align: "center",
		cellRenderer: ({ cellData }) => {
			// console.log('备注栏的cellData',cellData)
			return h("DIV", null, virtualTableTagShowing(cellData));
		},
	},
	{
		key: "operate",
		dataKey: "operate",
		title: "操作",
		width: "15%",
		align: "center",
		cellRenderer: ({ rowData }) =>
			h(
				ElButton,
				{
					type: "danger",
					onClick: () =>
						openDeleteKnowledgeOnlineFileConfirmBox(rowData.id),
					disabled: !isCanModify.value,
				},
				{ default: () => "删除" }
			),
	},
];
// 获取当前用户的权限
function getAllKnowledgeAPIInfo(isOnMounted = false) {
	getAllKnowledgeAPI().then(async (res) => {
		// console.log(res)
		// console.log(res.data)
		knowledgeArr.value = res.data;
		activeKnowledgeId.value = activeKnowledgeId.value
			? activeKnowledgeId.value
			: (knowledgeArr.value[0] && knowledgeArr.value[0].id) || 0;
		// console.log(activeKnowledgeId.value)
		// console.log(knowledgeTitle.value)
		if (isOnMounted)
			knowledgeTitle.value = knowledgeArr.value.find(
				(k) => k.id === activeKnowledgeId.value
			).name;
		verifyIsCanModify();
		// 进入页面就获取第一个的数据展示，先获取一次状态，同时开启轮询获取最新状态
		// new
		const response = await getAllFilesWithStatusAPI(
			activeKnowledgeId.value
		);
		documentDocList.value = response[0].data;
		console.log("documentDocList.value",documentDocList.value)
		// console.log('doc',documentDocList.value)
		changeAllFileStatus(response[1].data);
		// if(documentDocList.value.length >= 1){
		pollKnowledgeAllFilesStatusAPI(
			activeKnowledgeId.value,
			documentDocList.value
		);
		// }
	});
}
const userStore = useUserStore();
userStore.getInfo().then((userInfo) => {
	currentUser.value = {
		roleKey: userInfo.user.roles[0].roleKey,
		userId: userInfo.user.userId,
		userName: userInfo.user.userName,
	};
	console.log("当前用户为", currentUser.value);
});
function changeAllFileStatus(statusArr) {
	const len = documentDocList.value.length;
	for (let i = 0; i < len; ++i) {
		const { id } = documentDocList.value[i];
		if (id === "创建中") continue;
		const arr = statusArr.find((file) => file.id === id);
		if (!arr) continue;
		const { status } = arr;
		// 防止gray被改变为red等，要展示解析中的状态，但是轮询结束了还没好就要展示失败
		// if( documentDocList.value[i].status === 'gray') continue
		documentDocList.value[i].status = status;
	}
}
const onListenerWindowSizeChangeWithThrottle = throttle(
	onListenerWindowSizeChange,
	500
);
// 加入监听，动态设置虚拟表格的
onMounted(() => {
	const leftWidth = knowledgeOptionsRef.value.offsetWidth;
	const windowHeight = window.innerHeight;
	const windowWidth = window.innerWidth;
	knowledgeOptionsScrollHeight.value = windowHeight - 50 - 90;
	virtualTableHeight.value = windowHeight - 170;
	virtualTableWidth.value = windowWidth - leftWidth - 90;
	window.addEventListener("resize", onListenerWindowSizeChangeWithThrottle);
});

onBeforeUnmount(() =>
	window.removeEventListener("resize", onListenerWindowSizeChangeWithThrottle)
);

function onListenerWindowSizeChange() {
	const leftWidth = knowledgeOptionsRef.value.offsetWidth;
	const windowHeight = window.innerHeight;
	const windowWidth = window.innerWidth;
	knowledgeOptionsScrollHeight.value = windowHeight - 50 - 90;
	virtualTableHeight.value = windowHeight - 170;
	virtualTableWidth.value = windowWidth - leftWidth - 90;
}

// 进入页面获取知识库信息赋值
getAllKnowledgeAPIInfo(true);
// 有一个知识库标签改名了
EventBus.on("renameKnowledge", function ({ id, newName, type }) {
	if (activeKnowledgeId.value == id) {
		knowledgeTitle.value = newName;
	}
	updateKnowledgeAPI(id, newName, type);
});
// 删除某个知识库，删除的是active知识库就要变成第一个
EventBus.on("removeKnowledge", async function ({ id }) {
	const isActive = activeKnowledgeId.value == id;
	ElMessageBox.confirm(`是否删除文档`, {
		confirmButtonText: "确认",
		cancelButtonText: "取消",
		type: "warning",
	}).then(async () => {
		if (isActive) {
			closePollById(id);
		}
		// if (isActive) {
		//     const len = knowledgeArr.value.length
		//     const idx = knowledgeArr.value.findIndex(k=>k.id == id)
		//     let pre  = undefined
		//     let nex = undefined
		//     if(len === 1) activeKnowledgeId
		//     if(idx === 0){
		//         nex = knowledgeArr.value[idx+1]
		//     }else if(idx < len-1){
		//         nex = knowledgeArr.value[idx+1]
		//     // 说明点击的是最后一个
		//     }else if(idx === len-1){
		//         pre = knowledgeArr.value[idx-1]
		//     }
		//     console.log(pre,nex)
		// }
		await removeKnowledgeByIdAPI(id);
		ElMessage({
			type: "success",
			message: "删除知识库成功",
		});
		const idx = knowledgeArr.value.findIndex((item) => item.id == id);
		knowledgeArr.value.splice(idx, 1);
		await getAllKnowledgeAPIInfo();
		if (isActive) {
			activeKnowledgeId.value =
				knowledgeArr.value.length > 0 ? knowledgeArr.value[0].id : 0;
			knowledgeTitle.value =
				knowledgeArr.value.length > 0
					? knowledgeArr.value[0].name
					: "默认知识库";
			documentDocList.value =
				knowledgeArr.value.length > 0 ? knowledgeArr[0] : [];
		}
		// 如果删除的是当前点击的知识库，那么要切换为下一个位置的知识库
		// if(isActive){
		//     console.log('here',pre? pre.id : nex.id)
		//     activeKnowledgeId.value = pre? pre.id : nex.id
		//     knowledgeTitle.value = pre? pre.name : nex.name
		//     documentDocList.value = knowledgeArr.find(doc=>doc.id == activeKnowledgeId.value)
		// }
	});
});
EventBus.on("switchType", async function ({ id, name, type }) {
	await updateKnowledgeAPI(id, name, type);
	// 然后将当前的这个给改变状态
	const idx = knowledgeArr.value.findIndex((item) => item.id === id);
	knowledgeArr.value[idx].type = type;
});
onBeforeUnmount(() => {
	EventBus.all.clear();
});
// 没用上，我也不敢动
function removeDocument() {}
// 点击知识库标签切换后，需要将非当前知识库id的轮询关闭
async function changeKnowledgeDocument(e) {
	// console.log(e.target)
	let dom = e.target;
	while (
		dom.parentElement &&
		![...dom.classList].includes("knowledge-option-hover")
	) {
		dom = dom.parentElement;
	}
	// if (e.target.nodeName !== 'SPAN') return
	// console.log(dom)
	activeKnowledgeId.value = parseInt(dom.dataset.id);
	// activeKnowledgeId =
	console.log(parseInt(dom.dataset.id));
	const name = dom.textContent.slice(2);
	// console.log(name)
	if (name === "") return;
	knowledgeTitle.value = name;
	const loadingInstance = startLoading(virtualTable.value);
	// 判断是切换过去的知识库是否可以被该用户增删改
	verifyIsCanModify();
	let res;
	// 请求所点击的这个文档的列表信息
	try {
		res = await getAllFilesWithStatusAPI(activeKnowledgeId.value);
	} catch (err) {
		closeLoading(loadingInstance);
	}
	documentDocList.value = res[0].data;
	changeAllFileStatus(res[1].data);
	closeLoading(loadingInstance);
	closePollExcept(activeKnowledgeId.value);
	// console.log('优化性能看这里:',documentDocList.value)
	// 没有文件  或者  文件状态全部确定   就不要轮询
	if (
		documentDocList.value.length === 0 ||
		isAllFileStatusConfirmed(documentDocList.value)
	) {
		return;
	}
	pollKnowledgeAllFilesStatusAPI(
		activeKnowledgeId.value,
		documentDocList.value
	);
}
// 以后加网络请求，这里只有名字
async function addKnowledge() {
	// console.log('输入的是', addKnowledgeInpValue.value,addKnowledgeTypeValue.value)
	if (
		addKnowledgeInpValue.value === "" ||
		(addKnowledgeTypeValue.value !== 0 && addKnowledgeTypeValue.value !== 1)
	) {
		ElMessage({
			type: "warning",
			message: "请输入完整信息",
		});
		return;
	}
	await addNewKnowledgeAPI(
		null,
		addKnowledgeInpValue.value,
		addKnowledgeTypeValue.value
	);
	// console.log(res)
	// 重新获取一遍最新的数据，避免
	getAllKnowledgeAPIInfo();
	addKnowledgeInpValue.value = "";
	addKnowledgeDialogVisible.value = false;
}
function changeBgMouseOver(e) {
	if (
		e.target.nodeName === "DIV" &&
		[...e.target.parentElement.classList].includes("container")
	) {
		const bg = e.target.parentElement.style.backgroundColor;
		// 只要不等于被选中的颜色，就改变为hover颜色
		if (bg && bg !== "rgb(108, 143, 233)") {
			e.target.parentElement.style.backgroundColor = "rgb(215, 222, 242)";
		}
	}
}
function changeBgMouseLeave(e) {
	if (
		e.target.nodeName === "DIV" &&
		[...e.target.classList].includes("container")
	) {
		// 只要不等于被选中的颜色，就恢复原色
		const bg = e.target.style.backgroundColor;
		if (bg && bg !== "rgb(108, 143, 233)") {
			e.target.style.backgroundColor = "rgb(235, 237, 248)";
		}
	}
}
// 统计当前选择了多少文件，文件数目和uploadFileList.value一致了就可以打包发送了
let sum = 0;
// 点击全部上传后的请求会走这个
async function UploadFileHTTP(option) {
	// console.log('option', option)
	++sum;
	const { file } = option;
	const { uid } = file;
	// console.log('option', option)
	// 我直接在uploadFileList查找看有没有这个uid，有的话取出它的信息
	const {
		organization,
		date,
		tag: tagArr,
	} = uploadFileList.value.find(({ file }) => file.uid === uid);
	// 把这个tag从对象数组变为对象
	console.log("uploadFileList.value:", uploadFileList.value);
	// console.log()
	let tag = {};
	for (let i = 0; i < tagArr.length; ++i) {
		// tag[tagArr[i].tagName] = tagArr[i].tagContent;
		tag[tagArr[i].tagName] = tagArr[i].tagContent;
	}
	tagMap.set(uid, tag);
	// 保证全部加入后一次性发送，实现合并发送
	if (sum === uploadFileList.value.length) {
		uploadFileList.value.forEach((item) => {
			documentDocList.value.unshift({
				id: "创建中",
				name: item.file.name,
				date: item.date,
				institution: item.organization,
				status: "gray",
				tag: null,
			});
		});
		const uploadKnowledgeIdx = activeKnowledgeId.value;
		console.log("uploadFileList", uploadFileList.value);
		const fileInfoList = uploadFileList.value.map((item) => ({
			filename: item.file.name,
			date: item.date,
			institution: item.organization,
			tag: JSON.stringify(tagMap.get(item.file.uid)),
		}));
		const fileList = uploadFileList.value.map((item) => item.file.raw);
		await uploadKnowledgeFileAPI(
			activeKnowledgeId.value,
			fileInfoList,
			fileList
		);
		// 加一个判定，如果此时切换了知识库，那么接下来的轮询都不用做了，会耽误浏览器最多的并发请求数量
		if (activeKnowledgeId.value != uploadKnowledgeIdx) return;
		// 获取到当前该活跃数据库的最新文档数据，但是还要轮询它的上传状态
		const response = await getAllFilesWithStatusAPI(
			activeKnowledgeId.value
		);
		documentDocList.value = response[0].data;
		changeAllFileStatus(response[1].data);
		pollKnowledgeAllFilesStatusAPI(
			activeKnowledgeId.value,
			documentDocList.value
		);
	}
}
// 网路请求成功
function UploadFileSuccessfully(response, uplodaFile, _) {
	// console.log('成功上传的uid', response)
	if (sum === uploadFileList.value.length && sum > 0) {
		ElMessage({
			type: "success",
			message: "文件上传中",
		});
		uploadFileList.value = [];
		chosenFiles.value = [];
		sum = 0;
		tagMap.clear();
		console.log(tagMap);
	}
}
function UploadFileError(err) {
	uploadFileList.value = [];
	chosenFiles.value = [];
	sum = 0;
	tagMap.clear();
	console.log(tagMap);
}
function UploadAllFile() {
	if (uploadFileList.value.length === 0) {
		ElMessage({
			type: "warning",
			message: "请添加文件",
		});
		return;
	}
	uploadRef.value.submit();
	upLoadDialogVisible.value = false;
}
// 多选时，每个文件都会触发一次，但是点击全部上传后也会被触发一次
function selectLocalFiles(uploadFile, upLoadFiles) {
	console.log("选择的文件是", uploadFile);
	// 这可以保证只是选择添加文件阶段
	if (uploadFile.status !== "ready") return;
	chosenFiles.value.push(uploadFile);
}
function addFile() {
	if (!validate(inpOrganizationValue.value, inpDataPickerValue.value)) return;
	chosenFiles.value.forEach((file) => {
		uploadFileList.value.unshift({
			file,
			organization: inpOrganizationValue.value,
			date: inpDataPickerValue.value,
			// tag是数组，包含所有标签的kv
			tag: tagArr.value,
		});
	});
	// console.log(uploadFileList.value)
	inpOrganizationValue.value = "";
	inpDataPickerValue.value = "";
	chosenFiles.value = [];
	tagArr.value = [];
}
// 删除本地上传的文件
function deleteChosenFile(name) {
	const idx = chosenFiles.value.findIndex((file) => file.name === name);
	chosenFiles.value.splice(idx, 1);
}

// 找到name文件名一样的删除
async function UploadTabelOnClick(row, col, e) {
	// 点击删除才删除
	if (e.target.nodeName !== "SPAN" || e.target.textContent !== "删除") return;
	// 文件名
	const { name } = row;
	// await deleteKnowledgeFileAPI(activeKnowledgeId.value,'fileIds')
	const idx = uploadFileList.value.findIndex(
		({ file }) => file.name === name
	);
	uploadFileList.value.splice(idx, 1);
}
function CancelUploadAllFile() {
	uploadFileList.value = [];
	chosenFiles.value = [];
	upLoadDialogVisible.value = false;
}
// 可以在这里里卡住，不在uploadFileList内的文件不上传，通过uid判断
// 并且还要限制上传的类型
function beforeUploadFileCheck(uploadFile) {
	// console.log(uploadFile)
	const { uid, name } = uploadFile;
	// console.log('before',file.type)
	if (!validateUploadFileType(name)) {
		// 保证多个文件总共提醒一次
		if (!hasElMessage) {
			hasElMessage = true;
			ElMessage({
				type: "warning",
				message:
					"仅支持上传md,txt,pdf,jpg,png,jpeg,docx,xlsx,pptx,emk,csv文件",
			});
			setTimeout(() => (hasElMessage = false), 3000);
		}
		// 如果只有不满足的文件，那么会滞留，我们需要删除
		// 过滤掉不满足的文件
		uploadFileList.value = uploadFileList.value.filter(
			({ file }) => file.raw.uid !== uid
		);
		return false;
	}
	const isExist = uploadFileList.value.find(({ file }) => file.uid === uid)
		? true
		: false;
	console.log(`${uid}这个文件存在吗`, isExist);
	// 不存在说明这个文件不是被放到上传表格内的
	if (!isExist) return false;
	return true;
}

// 删除的提醒函数
function openDeleteKnowledgeOnlineFileConfirmBox(id) {
	if (id === "创建中") {
		ElMessage({
			type: "warning",
			message: "上传中文件无法删除",
		});
		return;
	}
	ElMessageBox.confirm("确认删除该文档吗?", {
		confirmButtonText: "确认",
		cancelButtonText: "取消",
	})
		.then(async () => {
			// console.log('要删除的文件id:',id,typeof id)
			try {
				await deleteKnowledgeOnlineFile(id);
				ElMessage({
					type: "success",
					message: "删除成功",
				});
			} catch (error) {
				console.log(error);
			}
		})
		.catch((err) => {
			// console.log('要删除的文件id:',id,typeof id)
			console.log(err);
		});
}

async function deleteKnowledgeOnlineFile(id) {
	// 避免同一个提交多次
	if (isDelKnowledgeIdArr.includes(id)) {
		// console.log('多次触发')
		return;
	}
	isDelKnowledgeIdArr.push(id);
	try {
		await deleteKnowledgeFileAPI(activeKnowledgeId.value, id);
	} catch (err) {
		console.log("删除文件报错:", err);
		throw new Error("删除文件失败");
	} finally {
		const idx = isDelKnowledgeIdArr.findIndex((idd) => (idd = id));
		isDelKnowledgeIdArr.splice(idx, 1);
	}
	getAllKnowledgeAPIInfo();
}

function addLabel() {
	if (tagNameValue.value !== "" && tagContentValue.value !== "") {
		tagArr.value.unshift({
			tagName: tagNameValue.value,
			tagContent: tagContentValue.value,
		});
		tagNameValue.value = "";
		tagContentValue.value = "";
	} else {
		ElMessage({
			type: "warning",
			message: "请输入完整标签信息",
		});
	}
}
// 删除标签，通过tagName找到这个标签并删除
function deleteLabel(tagName) {
	console.log(tagName);
	const labelIdx = tagArr.value.findIndex(
		(label) => label.tagName === tagName
	);
	tagArr.value.splice(labelIdx, 1);
}
// 只有创建者或者超级管理员才可以展示操作
function verifyIsCanModify() {
	// 获取这个知识库的创建者
	const creator = knowledgeArr.value.find(
		(k) => k.id === activeKnowledgeId.value
	).creator;
	console.log("这个知识库的创建者为:", creator);
	console.log("cc", currentUser.value);
	// 只有  创建者  或者  超级管理员  才可以展示操作
	if (
		creator === currentUser.value.userName ||
		currentUser.value.roleKey === "admin"
	) {
		isCanModify.value = true;
		return;
	}
	isCanModify.value = false;
	console.log("isCanModify", isCanModify.value);
}
</script>

<style scoped lang="scss">
.container {
	display: flex;
	width: 100%;
	height: calc(100vh - 50px);

	.knowledge-options {
		position: relative;
		flex: 2;
		background-color: #f5f7fa;

		.add-knowledge {
			position: relative;
			display: flex;
			align-items: center;
			position: absolute;
			top: 40px;
			width: 100%;
			padding: 0 15px;

			:deep() {
				.el-dialog__body {
					display: flex;
					// flex-direction: column;
					align-items: center;
					justify-content: space-between;
					padding: 20px 20px 35px 10px;

					.add-knowledge-inp {
						flex: 4;
						padding: 0 10px;
					}

					.add-knowledge-type-selector {
						flex: 2;
						padding: 0 10px 0 0;
					}

					.add-knowledge-btn {
						padding: 0 10px;
						width: 70px;
					}
				}
			}

			.title-and-add-btn {
				position: relative;
				display: flex;
				justify-content: center;
				align-items: center;
				width: 100%;

				.left-title {
					font-size: 30px;
					font-weight: 700;
				}

				.add-knowledge-btn {
					transform: translateX(20px);
				}
			}

			:deep() {
				.el-input__wrapper {
					padding-right: 1px;
				}
			}
		}

		.knowledge-option {
			position: relative;
			// margin-top:-10px;
			padding: 10px 20px;
			cursor: pointer;
		}
	}

	.scroll {
		position: absolute;
		top: 90px;
		width: 100%;
		background-color: #f5f7fa;
	}

	.knowledge-table {
		position: relative;
		flex: 9;
		background-color: #f2f2fd;

		.knowledge-title {
			position: absolute;
			top: 40px;
			left: 50px;
			font-size: 30px;
		}

		.upload-document-btn {
			position: absolute;
			width: 100px;
			height: 50px;
			right: 40px;
			top: 30px;
		}

		.upload-document-dialog {
			position: relative;

			.added-label {
				display: inline-block;
				margin: 0 10px 10px 0;
			}

			.upload-cancel {
				position: absolute;
				right: 130px;
				bottom: 10px;
			}

			.upload-card {
				position: relative;
				display: flex;
				flex-direction: column;
				width: 100%;
				top: -15px;
				// background-color: red;
				justify-content: center;

				// align-items: center;
				.upload-card-column-1 {
					display: flex;
					justify-content: center;
					align-items: center;
					flex: 1;
					margin-bottom: 10px;

					// .upload-document-region-btn {
					//     // transform: scale(1.1,2.2);
					// }
					// :deep(){
					//     .el-upload {
					//         height: 300px;
					//         color: red;
					//     }
					// }
					.data-picker {
						position: relative;
						right: 5px;
						flex: 5;
					}

					.upload-document-region {
						margin-left: 10px;
					}

					.upload-organization {
						flex: 5;
						margin: 0 10px;
					}
				}

				.upload-card-column-2 {
					position: relative;
					display: flex;
					align-items: center;
					justify-content: center;
					padding: 0 110px 0 10px;
					flex: 1;

					.tag-name {
						flex: 1;
						padding-right: 10px;
					}

					.tag-content {
						flex: 1;
					}

					.add-label-btn {
						position: absolute;
						width: 100px;
						right: 0;
						top: 0;
					}
				}

				.upload-card-column-3 {
					position: relative;
					width: 100%;
					flex: 1;

					.upload-add-btn {
						float: right;
						margin-top: 10px;
						width: 100px;
					}
				}

				// .upload-document-region {
				//     margin-right: 10px;
				// }
			}

			.chosen-files-box {
				width: 100%;

				// background-color: lightgray;
				.chosen-file {
					display: flex;
					align-items: center;
					position: relative;
					width: 100%;
					height: 22px;

					.chosen-file-progress {
						position: absolute;
						top: 50%;
						transform: translateY(-50%);
						right: 100px;
						width: 50%;
					}

					.chosen-file-del-btn {
						position: absolute;
						cursor: pointer;
						right: 30px;

						&:hover {
							color: red;
						}
					}
				}
			}

			.uploaded-file-list {
				width: 100%;
				margin: 10px 0 20px 0;

				.uploaded-file-delete-btn {
					pointer-events: none;
					cursor: pointer;

					&:hover {
						color: red;
					}
				}
			}

			.upload-confirm {
				position: absolute;
				bottom: 10px;
				right: 20px;
			}
		}

		.document-table {
			position: absolute;
			top: 100px;
			left: 20px;
			width: 100%;
			padding: 0 30px;

			.virtual-table-header {
				width: calc(100vw - 40px);
			}

			:deep() {
				.el-auto-resizer {
					.el-table-v2 {
						.el-table-v2__table {
							.el-table-v2__header-wrapper {
								background-color: #e9edf7;

								.el-table-v2__header-cell {
									background-color: #e9edf7;
								}
							}
						}
					}
				}
			}
		}
	}
}
</style>
