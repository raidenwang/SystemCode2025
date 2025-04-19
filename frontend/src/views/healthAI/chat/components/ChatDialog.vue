<script lang="ts" setup>

import MarkdownIt from "markdown-it";
import useUserStore from "../../../../store/modules/user.js";
import { onBeforeUpdate, watch, ref } from "vue";
import { ChatDialogPropsArray } from "./types.js"
const props = defineProps<ChatDialogPropsArray>();

let avatarUrl = "";
const userStore = useUserStore();
// 获取用户头像
userStore.getInfo().then((userInfo) => {
	avatarUrl = userInfo.user.avatar;
});
//markdown语法解析
const markdownParser = new MarkdownIt();
function renderMarkdown(text) {
	return markdownParser.render(text);
}
// 这里获取到的props是新的props
// onBeforeUpdate(() => {
// 	console.log("props:", props);
// });

// 联动触发这个btn，隐藏下方的所有来源展示
function openAllSource(e) {
	let times = 0;
	let dom = e.target;
	let parent = dom;
	while (dom.tagName !== "LABEL" || ![...dom.classList].includes("find-me")) {
		// console.log(dom, dom.tagName);
		if (times >= 5) return;
		parent = parent.parentNode;
		dom = parent;
		times++;
	}
	dom = dom.parentNode.querySelector(".click-me");
	// console.log("dom", dom);
	dom.click();
}

function switchArrow(e) {
	let times = 0;
	let dom = e.target;
	if (dom.tagName === "SPAN" && [...dom.classList].includes("el-text")) {
		let parent;
		parent = dom.parentNode.parentNode.parentNode;
		dom = parent.querySelector(".el-icon");
		dom.classList.toggle("active");
		return;
	}
	if (!["I", "path", "svg", "INPUT"].includes(dom.tagName)) return;
	let parent = dom.parent;
	while (dom.tagName !== "I" || ![...dom.classList].includes("el-icon")) {
		if (times >= 4) return;
		parent = parent?.parentNode || dom;
		dom = parent;
		times++;
	}
	dom.classList.toggle("active");
}
</script>

<template>
	<div class="container">
		<div class="msg" v-for="(item, i) in props.propsArray" :key="item.id">
			<div class="user-dialog" v-if="item.idClass == 0">
				<div class="avatar">
					<el-avatar
						:src="avatarUrl || 'src/assets/images/profile.jpg'"
					/>
					<span class="time">{{ item.createTime }}</span>
				</div>
				<div class="content">
					<span>{{ item.content }}</span>
				</div>
			</div>
			<div class="ai-dialog" v-else>
				<div class="avatar">
					<el-avatar>AI</el-avatar>
					<span class="time">{{ item.createTime }}</span>
				</div>
				<Fragment class="gray-bg">
					<div
						class="content"
						:class="{
							'full-border-radius':
								!item.chatSourceList ||
								item.chatSourceList.length <= 0 ||
								item.chatSourceList[0].score == null,
						}"
						v-html="renderMarkdown(item.content)"
					/>
					<div
						class="chat-source-list"
						v-if="
							item.chatSourceList &&
							item.chatSourceList.length > 0 &&
							item.chatSourceList[0].score != null
						"
					>
						<div class="source-sum-box">
							<span class="source-sum-text"
								>找到了{{
									item.chatSourceList.length
								}}个信息来源</span
							>
							<input
								type="checkbox"
								:id="`open-all-source-${
									item.createTime + item.id
								}`"
								class="open-all-source-btn"
							/>
							<label
								:for="`open-all-source-${item.id}`"
								@click="openAllSource"
								class="find-me"
							>
								<el-icon class="source-sum-icon"
									><ArrowUpBold /></el-icon
							></label>
							<label
								:for="`open-all-source-${
									item.createTime + item.id
								}`"
								class="click-me"
							></label>
						</div>
						<input
							type="checkbox"
							:id="`open-all-source-${item.id}`"
							class="open-all-source-btn2"
						/>
						<Fragment class="all-source-box">
							<div
								class="one-source-box"
								v-for="(source, index) in item.chatSourceList"
								:key="index"
								@click="switchArrow"
							>
								<div class="one-source-title">
									<span class="one-source-title-text"
										><span class="gray"
											>数据来源{{ index + 1 }}:
										</span>
										<label
											:for="`one-source-content-btn-${
												source + index + i
											}`"
											><el-text class="doc-name">{{
												source.fileName
											}}({{ source.institution }})</el-text></label
										>
									</span>
									<label
										:for="`one-source-content-btn-${
											source + index + i
										}`"
									>
										<span class="one-source-icon">
											<el-icon
												><CaretBottom
											/></el-icon> </span
									></label>
								</div>
								<input
									type="checkbox"
									:id="`one-source-content-btn-${
										source + index + i
									}`"
									class="open-one-source-content"
								/>
								<div class="one-source-content show-animation">
									<div class="one-source-content-text">
										{{ source.content }}
									</div>
									<div class="correlation">
										<span class="gray">相关性:</span>
										{{ source.score }}
									</div>
								</div>
							</div>
						</Fragment>
					</div>
				</Fragment>
			</div>
		</div>
	</div>
</template>

<style lang="scss" scoped>
@keyframes gradually-show {
	from {
		transform: translateY(-40px);
		opacity: 0;
	}
	to {
		transform: none;
		opacity: 1;
	}
}
@keyframes gradually-hidden {
	from {
		transform: none;
		opacity: 1;
	}
	to {
		transform: translateY(-40px);
		opacity: 0;
	}
}
.container {
	width: 100%;
	height: 100%;
	.msg {
		margin: 20px 20px 20px 0;
		.user-dialog {
			.avatar {
				display: flex;
				align-items: center;
				flex-direction: row-reverse;
			}
			.content {
				display: flex;
				flex-direction: row-reverse;
				margin: 5px;
				border-radius: 20px;
				span {
					padding: 15px 10px;
					background-color: #b3e19d;
					border-radius: 10px;
				}
			}
			.time {
				margin-right: 20px;
				font-weight: 700;
			}
		}
		.ai-dialog {
			.time {
				margin-left: 20px;
				font-weight: 700;
			}
			.gray-bg {
				position: relative;
				.content {
					background-color: #e9e9eb;
					margin-top: 5px;
					padding: 5px 10px;
					border-radius: 20px 20px 0 0;
					&.full-border-radius {
						border-radius: 20px;
					}
				}
				.chat-source-list {
					background-color: #e9e9eb;
					padding: 5px 10px;
					border-radius: 0 0 20px 20px;
					.source-sum-box {
						display: flex;
						align-items: center;
						// background-color: red;
						margin-bottom: 10px;
						.source-sum-text {
							font-size: 16px;
							font-weight: 700;
						}
						.open-all-source-btn {
							display: none;
						}
						.open-all-source-btn:checked + label .source-sum-icon {
							transform: rotate(180deg);
						}
						.source-sum-icon {
							transition: all 0.3s ease;
							margin-left: 10px;
							cursor: pointer;
						}
					}
					.open-all-source-btn2 {
						display: none;
					}
					.open-all-source-btn2:checked + .all-source-box {
						display: block;
					}
					.all-source-box {
						display: none;
						transition: all 0.3s ease;
					}
					.one-source-box {
						// background-color: lightblue;
						margin-top: 20px;
						.one-source-title {
							display: flex;
							align-items: center;
							// background-color: #fff;
							.doc-name {
								color: #5a47e5;
								text-decoration: underline;
								cursor: pointer;
								font-size: 16px;
								font-weight: 400;
							}
							.one-source-icon {
								width: 100%;
								height: 100%;
								margin-left: 5px;
								cursor: pointer;
								transition: all 0.3s ease;
								.active {
									transform: rotate(180deg);
								}
							}
						}
						.gray {
							color: #777373;
						}
						.open-one-source-content {
							display: none;
						}
						.one-source-content {
							transition: all 0.3s ease;
							display: none;
							.one-source-content-text {
								margin: 25px 0;
							}
						}
						.show-animation {
							animation: gradually-show 0.4s;
						}
						.hide-animation {
							animation: gradually-hidden 0.4s;
						}
						.open-one-source-content:checked + .one-source-content {
							display: block;
						}
					}
				}
			}
		}
	}
}
</style>
