<script setup>
import { ref } from "vue";
import { getPrompt } from "@/api/chat/chat";
import EventBusChat from "../utils/eventBus";
import { QuestionFilled } from "@element-plus/icons-vue";
const promptList = ref([]);
const currentAnimation = ref("gradually-showup");
getPrompt().then((res) => {
	promptList.value = res.data;
});
function selectPlugin(pluginId) {
	currentAnimation.value = "gradually-hidden";
	setTimeout(() => {
		EventBusChat.emit("select-plugin", pluginId);
	}, 300);
}
</script>

<template>
	<div class="container">
		<Teleport to=".inserted">
			<div class="teleport" :class="currentAnimation">
				<div class="close-icon" @click="() => selectPlugin(undefined)">
					<el-icon><Close /></el-icon>
				</div>
				<div class="title">请选择插件</div>
				<el-popover
					placement="right"
					title="说明"
					:width="200"
					trigger="hover"
					content="您可以选择辅助插件，实现辅助医生开检查检验单、异常指标的解读、疾病之间的辅助鉴别、疾病风险分层/级以及药品的禁忌检查。"
				>
					<template #reference>
						<el-icon
							size="20"
							:style="{
								position: 'absolute',
								top: '10px',
								right: '40%',
							}"
						>
							<QuestionFilled />
						</el-icon>
					</template>
				</el-popover>
				<el-scrollbar class="prompt-list" height="100%">
					<Fragment class="flex-col">
						<div
							class="prompt"
							v-for="item in promptList"
							:key="item.id"
						>
							<el-popover
								placement="right"
								title="说明"
								:width="200"
								trigger="hover"
								:content="item.introduce"
							>
								<template #reference>
									<el-icon
										size="20"
										:style="{
											position: 'absolute',
											top: '10px',
											right: '40%',
										}"
									>
										<QuestionFilled />
									</el-icon>
								</template>
							</el-popover>
							<el-button
								type="primary"
								class="prompt-btn"
								@click="() => selectPlugin(item.id)"
								>{{ item.name }}</el-button
							>
						</div>
					</Fragment>
				</el-scrollbar>
			</div>
		</Teleport>
	</div>
</template>

<style lang="scss" scoped>
@keyframes graduallyShowUp {
	0% {
		top: 0;
	}
	100% {
		top: 50%;
	}
}
@keyframes graduallyHidden {
	0% {
		top: 50%;
		opacity: 1;
	}
	50% {
		opacity: 0;
	}
	75% {
		top: -40%;
		opacity: 0;
	}
	100% {
		top: -80%;
		opacity: 0;
	}
}
.container {
	position: fixed;
	left: 0;
	top: 0;
	width: 100vw;
	height: 100vh;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 99;
	.teleport {
		position: fixed;
		display: flex;
		flex-direction: column;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 60vw;
		height: 60vh;
		border-radius: 20px;
		background-color: rgba(0, 0, 0, 0.5);
		z-index: 99;
		.close-icon {
			position: absolute;
			top: 10px;
			right: 10px;
			font-size: 20px;
			cursor: pointer;
		}
		&.gradually-showup {
			animation: graduallyShowUp 0.6s ease;
		}
		&.gradually-hidden {
			animation: graduallyHidden 0.4s ease;
		}
		.title {
			display: flex;
			align-items: center;
			flex: 1;
			font-size: 20px;
			border-radius: 20px;
			justify-content: center;
			background-color: rgba(255, 255, 255, 0.9);
			user-select: none;
		}
		.prompt-list {
			flex: 9;
			.flex-col {
				display: flex;
				flex-direction: column;
				justify-content: center;
				.prompt {
					position: relative;
					width: 100%;
					padding: 10px;
					flex: 1;
					display: flex;
					justify-content: center;
					.prompt-btn {
						width: 100%;
						height: 50px;
						font-size: 18px;
					}
				}
			}
		}
	}
}
</style>
