<!-- DiseaseSymptomSelector/index.vue -->
<template>
  <div class="disease-selector">
    <div class="header">
      <h2>{{ diseasePart }} 疾病症状</h2>
      <button class="close-btn" @click="onCancel">
        &times;
      </button>
      <!-- 添加警告提示 -->
      <transition name="fade">
        <div v-if="showEmptyWarning" class="warning-message">
          <svg class="warning-icon" viewBox="0 0 24 24">
            <path d="M12 2L1 21h22L12 2zm0 3.5L19.5 19h-15L12 5.5z"/>
            <path d="M12 16c.6 0 1-.4 1-1s-.4-1-1-1-1 .4-1 1 .4 1 1 1zm-1-5h2v-4h-2v4z"/>
          </svg>
          症状描述不能为空
        </div>
      </transition>
    </div>
    
    <div v-if="loading" class="loading-state">
      <div class="loader-small"></div>
      <p>正在加载症状数据...</p>
    </div>

    <div v-else class="content">
      <!-- 症状选项区域 -->
      <div class="options-section">
        <template v-for="(options, part) in availableOptions" :key="part">
          <div class="option-group" v-if="part !== '其他'">
            <div class="option-header" @click="toggleExpand(part)">
              <h3>{{ part }}</h3>
              <span class="expand-icon">{{ expandedParts.has(part) ? '−' : '+' }}</span>
            </div>
            <transition name="slide">
              <div v-show="expandedParts.has(part)" class="option-content">
                <select 
                  multiple
                  v-model="selected[part]"
                  @change="onSelectChange(part)"
                  class="symptom-select"
                >
                  <option 
                    v-for="option in options" 
                    :key="option" 
                    :value="option"
                  >
                    {{ option }}
                  </option>
                </select>
              </div>
            </transition>
          </div>
        </template>
      </div>
      
      <!-- 文本输入区域 -->
      <div class="input-section">
        <textarea
          v-model="inputText"
          rows="8"
          class="symptom-textarea"
          placeholder="请输入症状描述..."
        ></textarea>
        
        <!-- 自定义症状输入 -->
        <div class="custom-input-group" v-if="availableOptions['其他']">
          <h3>其他症状</h3>
          <div class="input-wrapper">
            <input
              type="text"
              v-model="customSymptom"
              placeholder="请输入其他症状..."
              class="custom-input"
              @keyup.enter="addCustomSymptom"
            />
            <button @click="addCustomSymptom" class="add-btn">+ 添加</button>
          </div>
        </div>
        
        <div class="button-group">
          <button class="btn save-btn" @click="onSave">
            保存症状
          </button>
          <button class="btn jump-btn" @click="onJump">
            跳转到诊断
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "DiseaseSymptomSelector",
  props: {
    diseasePart: String,
    availableOptions: Object,
    loading: Boolean
  },
  data() {
    return {
      inputText: localStorage.getItem("savedSymptoms") || "",
      selected: {},
      customSymptom: "",
      expandedParts: new Set(), // 存储展开的分组
      showEmptyWarning: false // 控制警告显示
    }
  },
  watch: {
    availableOptions: {
      immediate: true,
      handler(newVal) {
        this.initSelectedState(newVal)
      }
    }
  },
  methods: {
    initSelectedState(options) {
      this.selected = Object.keys(options).reduce((acc, key) => {
        acc[key] = []
        return acc
      }, {})
    },
    toggleExpand(part) {
      if (this.expandedParts.has(part)) {
        this.expandedParts.delete(part)
      } else {
        this.expandedParts.add(part)
      }
    },
    onSelectChange(part) {
      const newOptions = this.selected[part] || [];
      newOptions.forEach((option) => {
        if (!this.inputText.includes(option)) {
          this.inputText += option + " ";
        }
      });
    },
    addCustomSymptom() {
      if (this.customSymptom.trim()) {
        this.inputText += this.customSymptom + " ";
        this.customSymptom = "";
      }
    },
    showWarning() {
      this.showEmptyWarning = true;
      setTimeout(() => {
        this.showEmptyWarning = false;
      }, 3000);
    },
    onSave() {
      if (!this.inputText || this.inputText.trim() === "") {
        this.showWarning();
        return;
      }
      const symptoms = this.inputText.trim();
      localStorage.setItem("savedSymptoms", symptoms);
      this.$emit('save', symptoms);
    },
    onJump() {
      if (!this.inputText || this.inputText.trim() === "") {
        this.showWarning();
        return;
      }
      window.location.href = "http://localhost:70/guide/symptoms-confirm";
    },
    onCancel() {
      this.$emit("hide");
    },
  },
};
</script>

<style scoped>
.disease-selector {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 120, 255, 0.12);
  overflow: hidden;
  border: 1px solid #e0e8f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 28px;
  border-bottom: 1px solid #e8f1ff;
  background: linear-gradient(135deg, #f8fbff 0%, #e6f0ff 100%);
  position: relative;
}

.header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.4rem;
  font-weight: 600;
  letter-spacing: 0.5px;
  position: relative;
  padding-left: 12px;
}

.header h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: #4484ec;
  border-radius: 2px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #7f8c8d;
  padding: 4px;
  line-height: 1;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.close-btn:hover {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
  transform: rotate(90deg) scale(1.1);
}

.loading-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
  padding: 40px 0;
  background: rgba(255, 255, 255, 0.8);
}

.loader-small {
  border: 3px solid rgba(68, 132, 236, 0.2);
  border-top: 3px solid #4484ec;
  border-radius: 50%;
  width: 28px;
  height: 28px;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.content {
  display: flex;
  flex: 1;
  gap: 24px;
  padding: 24px;
  overflow: hidden;
  background: #f9fbfe;
}

.options-section {
  flex: 1;
  overflow-y: auto;
  padding-right: 12px;
  scrollbar-width: thin;
  scrollbar-color: #c4d8ff #f0f6ff;
}

.options-section::-webkit-scrollbar {
  width: 6px;
}

.options-section::-webkit-scrollbar-thumb {
  background-color: #c4d8ff;
  border-radius: 3px;
}

.options-section::-webkit-scrollbar-track {
  background: #f0f6ff;
}

.input-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 340px;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 60, 150, 0.05);
  border: 1px solid #e0e8f5;
}

.option-group {
  margin-bottom: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 60, 150, 0.05);
  border: 1px solid #e0e8f5;
  overflow: hidden;
}

.option-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background-color: #f8fbff;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
}

.option-header:hover {
  background-color: #f0f7ff;
}

.option-header h3 {
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0;
  position: relative;
  padding-left: 28px;
}

.option-header h3::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%234484ec'%3E%3Cpath d='M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z'/%3E%3C/svg%3E") center/contain no-repeat;
}

.expand-icon {
  font-size: 1.3rem;
  font-weight: 500;
  color: #7f8c8d;
  min-width: 24px;
  text-align: center;
  transition: all 0.3s ease;
}

.option-content {
  padding: 0 20px 16px;
}

.symptom-select {
  width: 100%;
  min-height: 140px;
  max-height: 220px;
  padding: 12px;
  border: 1px solid #e0e8f5;
  border-radius: 10px;
  background: #fff;
  font-size: 15px;
  color: #2c3e50;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 60, 150, 0.05);
  appearance: none;
  overflow: auto;
  line-height: 1.6;
}

.symptom-select:focus {
  outline: none;
  border-color: #4484ec;
  box-shadow: 0 0 0 3px rgba(68, 132, 236, 0.2);
}

.symptom-select option {
  padding: 10px 16px;
  margin: 6px 0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  background: #fff;
  border-left: 3px solid transparent;
}

.symptom-select option:hover {
  background: #f5f9ff;
  transform: translateX(4px);
  border-left-color: #4484ec;
}

.symptom-select option:checked {
  background: linear-gradient(135deg, #f0f7ff 0%, #e0eeff 100%);
  color: #2c3e50;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(68, 132, 236, 0.1);
}

.symptom-select option::before {
  content: '';
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #dfe6f0;
  border-radius: 4px;
  margin-right: 12px;
  vertical-align: middle;
  transition: all 0.2s ease;
}

.symptom-select option:checked::before {
  background: #4484ec url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='white'%3E%3Cpath d='M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z'/%3E%3C/svg%3E") center/12px no-repeat;
  border-color: #4484ec;
}

.symptom-textarea {
  flex: 1;
  width: 100%;
  padding: 16px;
  border: 1px solid #e0e8f5;
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.6;
  resize: none;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 60, 150, 0.05);
}

.symptom-textarea:focus {
  border-color: #4484ec;
  outline: none;
  box-shadow: 0 0 0 3px rgba(68, 132, 236, 0.2);
}

.custom-input-group {
  margin-bottom: 20px;
}

.custom-input-group h3 {
  margin: 0 0 14px 0;
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
  padding-left: 8px;
  position: relative;
}

.custom-input-group h3::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: #4484ec;
  border-radius: 2px;
}

.input-wrapper {
  display: flex;
  gap: 12px;
}

.custom-input {
  flex: 1;
  padding: 14px 16px;
  border: 1px solid #e0e8f5;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s ease;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 60, 150, 0.05);
}

.custom-input:focus {
  border-color: #4484ec;
  outline: none;
  box-shadow: 0 0 0 3px rgba(68, 132, 236, 0.2);
}

.add-btn {
  padding: 0 24px;
  background: linear-gradient(135deg, #4484ec 0%, #3a73d1 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  font-size: 15px;
  box-shadow: 0 2px 8px rgba(68, 132, 236, 0.2);
}

.add-btn:hover {
  background: linear-gradient(135deg, #3a73d1 0%, #2f62b9 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(68, 132, 236, 0.3);
}

.button-group {
  display: flex;
  gap: 16px;
  margin-top: auto;
}

.btn {
  padding: 14px 28px;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.save-btn {
  background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(46, 204, 113, 0.3);
}

.save-btn:hover {
  background: linear-gradient(135deg, #27ae60 0%, #219653 100%);
}

.jump-btn {
  background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(155, 89, 182, 0.3);
}

.jump-btn:hover {
  background: linear-gradient(135deg, #8e44ad 0%, #7d3c98 100%);
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  max-height: 500px;
  overflow: hidden;
}

.slide-enter,
.slide-leave-to {
  opacity: 0;
  max-height: 0;
  transform: translateY(-10px);
}

/* 新增警告提示样式 */
.warning-message {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: #fff3f3;
  color: #e74c3c;
  padding: 12px 24px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.2);
  display: flex;
  align-items: center;
  font-weight: 500;
  z-index: 1000;
  border: 1px solid #ffd6d6;
}

.warning-icon {
  width: 20px;
  height: 20px;
  fill: #e74c3c;
  margin-right: 10px;
}

.fade-enter-active, .fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
  transform: translate(-50%, -10px);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>