<template>
  <div class="textual-page">
    <div class="container">
      <h2 class="page-title">症状确认</h2>
      <div class="info-card">
        <h3 class="section-title">您选择的症状</h3>
        <div class="symptoms-display">
          <template v-if="symptomsWithDuration.length > 0">
            <div 
              v-for="(symptom, index) in symptomsWithDuration" 
              :key="index" 
              class="symptom-item"
            >
              <div class="symptom-name">{{ symptom.name }}</div>
              <div class="duration-select-wrapper">
                <select
                  v-model="symptom.duration"
                  class="duration-select"
                >
                  <option v-for="day in 30" :value="day" :key="day">{{ day }}天</option>
                </select>
                <i class="icon-chevron-down"></i>
              </div>
            </div>
          </template>
          <div v-else class="empty-state">
            <i class="icon-info"></i>
            尚未选择任何症状
          </div>
        </div>
      </div>

      <div class="info-card collapsible" :class="{ 'is-collapsed': isCollapsed }">
        <div class="section-header" @click="toggleCollapse">
          <h3 class="section-title">患者信息</h3>
          <i class="icon-chevron" :class="{ 'rotate-180': !isCollapsed }"></i>
        </div>
        <form 
          @submit.prevent="handleFormSubmit" 
          class="patient-form"
          v-show="!isCollapsed"
        >
          <div class="form-grid">
            <div class="form-item">
              <label for="name" class="form-label">姓名</label>
              <input
                id="name"
                v-model="formData.name"
                placeholder="请输入姓名"
                class="form-input"
                required
              />
            </div>

            <div class="form-item">
              <label for="age" class="form-label">年龄</label>
              <input
                id="age"
                v-model="formData.age"
                type="number"
                placeholder="请输入年龄"
                class="form-input"
                required
              />
            </div>

            <div class="form-item">
              <label for="weight" class="form-label">体重</label>
              <input
                id="weight"
                v-model="formData.weight"
                type="number"
                placeholder="请输入体重（kg）"
                class="form-input"
                required
              />
            </div>

            <div class="form-item">
              <label class="form-label">性别</label>
              <div class="radio-group">
                <label class="radio-item">
                  <input
                    type="radio"
                    v-model="formData.gender"
                    :value="1"
                    required
                  />
                  <span class="radio-label">男</span>
                </label>
                <label class="radio-item">
                  <input
                    type="radio"
                    v-model="formData.gender"
                    :value="0"
                    required
                  />
                  <span class="radio-label">女</span>
                </label>
              </div>
            </div>

            <div class="form-item full-width">
              <label for="allergy" class="form-label">过敏史</label>
              <input
                id="allergy"
                v-model="formData.allergy"
                placeholder="请输入过敏史（如无则留空）"
                class="form-input"
              />
            </div>

            <div class="form-item full-width">
              <label for="medicalHistory" class="form-label">过往病史</label>
              <input
                id="medicalHistory"
                v-model="formData.medicalHistory"
                placeholder="请输入过往病史（如无则留空）"
                class="form-input"
              />
            </div>
          </div>
        </form>
      </div>

      <!-- 单独放置的操作按钮 -->
      <div class="action-buttons-container">
        <button 
          type="button" 
          class="btn secondary"
          @click="handleAddSymptoms"
        >
          <i class="icon-add"></i>
          继续添加症状
        </button>
        <button 
          type="submit" 
          class="btn primary"
          @click="handleFormSubmit"
        >
          <i class="icon-check"></i>
          确认提交
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();
const isCollapsed = ref(true);

// 从 localStorage 中读取患者信息和症状
const selectedSymptoms = ref(localStorage.getItem("savedSymptoms") || "");
const formData = ref({
  name: "",
  age: "",
  weight: "",
  gender: null,
  allergy: "", // 过敏史
  medicalHistory: "", // 过往病史
  ...JSON.parse(localStorage.getItem("patientInfo") || "{}"),
});

// 将症状拆分为数组，并为每个症状添加持续时间输入框
const symptomsWithDuration = ref([]);

// 页面加载时初始化数据
onMounted(() => {
  const savedSymptoms = localStorage.getItem("savedSymptoms");
  if (savedSymptoms) {
    // 使用空格作为分隔符，将症状拆分为数组
    const symptomsArray = savedSymptoms.split(" ").filter((symptom) => symptom.trim());
    // 为每个症状添加持续时间输入框，默认1天
    symptomsWithDuration.value = symptomsArray.map((name) => ({
      name,
      duration: 1,
    }));
  }
});

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value;
};

// 处理添加症状并跳转
const handleAddSymptoms = () => {
  // 保存当前症状和持续时间
  const symptomsToSave = symptomsWithDuration.value
    .map((symptom) => `${symptom.name}（${symptom.duration || "未填写"}天）`)
    .join(" ");
  localStorage.setItem("savedSymptoms", symptomsToSave);
  window.location.href = "http://localhost/guide/patientInfo/human";
};

// 处理表单提交
const handleFormSubmit = async () => {
  // 更新患者信息
  const combinedData = {
    ...formData.value,
    symptoms: symptomsWithDuration.value
      .map((symptom) => `${symptom.name}（${symptom.duration || "未填写"}天）`)
      .join(" "),
    allergy: formData.value.allergy || "未提及", // 如果未填写过敏史，则默认为"未提及"
    medicalHistory: formData.value.medicalHistory || "未提及", // 如果未填写过往病史，则默认为"未提及"
  };
  localStorage.setItem("patientInfo", JSON.stringify(combinedData));

  // 调用 API
  const apiData = {
    model: "glm-4v-flash",
    messages: [
      { role: "system", content: "你是一个专业的医生,请根据我给出的患者信息，首先给出建议就诊科室（标题是建议就诊科室），然后生成患者的电子病历（标题名为电子病历），然后是给出初步诊断（标题初步诊断），最后是治疗建议（标题治疗建议）。" },
      { role: "user", content: JSON.stringify(combinedData) },
    ],
  };
  const config = {
    headers: {
      Authorization: "a7ab43a5f9194de788cd0711505136cb.u2ocKQpIY8MJ4nIf", // 替换为实际的 API Key
      "Content-Type": "application/json",
    },
  };

  try {
    const response = await axios.post(
      "https://open.bigmodel.cn/api/paas/v4/chat/completions",
      apiData,
      config
    );
    const message = response.data.choices[0].message.content;
    localStorage.setItem("diagnosisMessage", message);
    // 跳转到结果页面
    router.push({ path: "/guide/result" });
  } catch (error) {
    console.error("API请求失败", error);
  }
};
</script>

<style scoped>
.textual-page {
  padding: 2rem;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  min-height: 100vh;
  /* display: flex;
  flex-direction: column; */
}

.container {
  max-width: 800px;
  margin: 0 auto;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.page-title {
  text-align: center;
  color: #2c3e50;
  font-size: 2rem;
  margin-bottom: 2rem;
  font-weight: 600;
  position: relative;
  padding-bottom: 0.5rem;
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #3498db, #9b59b6);
  border-radius: 3px;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 1.5rem;
  transition: all 0.3s ease;
}

.info-card:hover {
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.12);
}

.collapsible .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 0.5rem 0;
}

.collapsible .section-header .icon-chevron {
  transition: transform 0.3s ease;
}

.collapsible .section-header .rotate-180 {
  transform: rotate(180deg);
}

.section-title {
  color: #3498db;
  font-size: 1.25rem;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #e9ecef;
}

.symptoms-display {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 1rem;
}

.symptom-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1rem;
  background: white;
  border-radius: 8px;
  margin-bottom: 0.75rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.symptom-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.symptom-name {
  color: #2c3e50;
  font-weight: 500;
}

.duration-select-wrapper {
  position: relative;
  width: 120px;
}

.duration-select {
  width: 100%;
  padding: 0.5rem 2rem 0.5rem 1rem;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  appearance: none;
  background-color: white;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.2s ease;
}

.duration-select:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
  outline: none;
}

.icon-chevron-down {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: #6c757d;
  font-size: 0.8em;
}

.empty-state {
  text-align: center;
  color: #6c757d;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.patient-form {
  margin-top: 1rem;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.form-item {
  margin-bottom: 1rem;
}

.form-item.full-width {
  grid-column: span 2;
}

.form-label {
  display: block;
  color: #495057;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.form-input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
  outline: none;
}

.radio-group {
  display: flex;
  gap: 1rem;
  padding: 0.5rem 0;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.radio-label {
  color: #495057;
  cursor: pointer;
}

/* 单独的操作按钮容器 */
.action-buttons-container {
  display: flex;
  gap: 1rem;
  margin-top: auto; /* 使按钮始终位于底部 */
  padding-top: 1.5rem;
  justify-content: flex-end;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn.primary {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: white;
}

.btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.btn.secondary {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
  color: #495057;
}

.btn.secondary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.icon-add,
.icon-check {
  width: 18px;
  height: 18px;
  fill: currentColor;
}
</style>