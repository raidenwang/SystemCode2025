<template>
  <div class="result-page">
    <div class="container">
      <div class="header">
        <h2>诊断结果与建议</h2>
        <p class="subtitle">基于您的症状分析得出的专业建议</p>
      </div>
      
      <div class="result-content">
        <el-card v-if="message" shadow="always" class="result-card">
          <!-- 就诊科室 - 重点突出 -->
          <div class="section department-section">
            <div class="section-header">
              <svg-icon name="hospital" class="section-icon" />
              <h3>建议就诊科室</h3>
            </div>
            <el-divider class="custom-divider" />
            <div class="department-content">
              <el-tag type="success" size="large" class="department-tag">
                {{ department }}
              </el-tag>
              <p class="department-tip">请尽快前往上述科室就诊</p>
            </div>
          </div>

          <!-- 可折叠的其他部分 -->
          <el-collapse v-model="activeNames" class="custom-collapse">
            <!-- 患者基本信息 -->
            <el-collapse-item name="1" title="患者基本信息">
              <pre class="content-box">{{ sections.patientInfo }}</pre>
            </el-collapse-item>
            
            <!-- 主诉症状 -->
            <el-collapse-item name="2" title="主诉症状">
              <pre class="content-box">{{ sections.symptoms }}</pre>
            </el-collapse-item>
            
            <!-- 初步诊断 -->
            <el-collapse-item name="3" title="初步诊断">
              <pre class="content-box">{{ sections.diagnosis }}</pre>
            </el-collapse-item>
            
            <!-- 治疗建议 -->
            <el-collapse-item name="4" title="治疗建议">
              <pre class="content-box">{{ sections.treatment }}</pre>
            </el-collapse-item>
          </el-collapse>
        </el-card>
        
        <div v-else class="no-result">
          <el-empty description="未获取到诊断结果" image="/images/empty-result.png">
            <el-button type="primary" @click="handleBackToHome">返回首页</el-button>
          </el-empty>
        </div>
      </div>

      <div class="button-group">
        <el-button type="primary" size="large" @click="handleBackToHome" class="action-btn">
          <svg-icon name="home" class="btn-icon" />
          返回首页
        </el-button>
        <el-button type="success" size="large" @click="handleAddMoreSymptoms" class="action-btn">
          <svg-icon name="add" class="btn-icon" />
          继续添加症状
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElCard, ElDivider, ElButton, ElEmpty, ElCollapse, ElCollapseItem, ElTag } from "element-plus";

const router = useRouter();
const message = ref("");
const activeNames = ref([]); // 控制折叠面板的展开项
const department = ref(""); // 就诊科室

// 定义各个部分的内容
const sections = computed(() => {
  if (!message.value) return {
    patientInfo: "",
    symptoms: "",
    diagnosis: "",
    treatment: ""
  };

  const content = formattedMessage.value;
  
  // 提取各个部分
  return {
    patientInfo: content.split("#### 患者基本信息：")[1]?.split("#### 主诉症状：")[0] || "",
    symptoms: content.split("#### 主诉症状：")[1]?.split("#### 初步诊断：")[0] || "",
    diagnosis: content.split("#### 初步诊断：")[1]?.split("### 治疗建议：")[0] || "",
    treatment: content.split("### 治疗建议：")[1] || ""
  };
});

// 格式化 AI 回答
const formattedMessage = computed(() => {
  if (!message.value) return "";

  // 去除多余的占位符（如 **）
  let formatted = message.value.replace(/\*\*/g, "");
  //formatted = message.value.replace(/\#\#/g, "");
  // 添加缩进和换行
  formatted = formatted
    .split("\n") // 按行分割
    .map((line) => {
      if (line.startsWith("1.") || line.startsWith("2.") || line.startsWith("3.")) {
        return `\n${line}`; // 添加空行分隔
      }
      if (line.startsWith(" - ")) {
        return `  ${line}`; // 添加缩进
      }
      return line;
    })
    .join("\n"); // 重新组合为字符串

  return formatted;
});

onMounted(() => {
  const savedMessage = localStorage.getItem("diagnosisMessage");
  if (savedMessage) {
    message.value = savedMessage;
    // 提取就诊科室
    const deptMatch = savedMessage.match(/### 建议就诊科室：\s*([^\n]+)/);
    if (deptMatch && deptMatch[1]) {
      department.value = deptMatch[1].trim();
    }
    localStorage.removeItem("diagnosisMessage"); // 读取后清除数据
    
  } else {
    console.error("未获取到诊断结果数据");
  }
});

// 返回首页
const handleBackToHome = () => {
  localStorage.removeItem("savedSymptoms");
  router.push("/guide/patientInfo");
};

// 继续添加症状
const handleAddMoreSymptoms = () => {
  window.location.href = "http://localhost/guide/patientInfo/human";
};
</script>

<style scoped lang="scss">
.result-page {
  padding: 40px 20px;
  background-color: #f8fafc;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.container {
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.header {
  text-align: center;
  margin-bottom: 30px;
  
  h2 {
    font-size: 28px;
    font-weight: 600;
    color: #2c3e50;
    margin-bottom: 8px;
  }
  
  .subtitle {
    font-size: 16px;
    color: #7f8c8d;
    margin: 0;
  }
}

.result-content {
  margin-bottom: 30px;
}

.result-card {
  padding: 25px;
  border-radius: 10px;
  border: none;
  
  :deep(.el-card__body) {
    padding: 0;
  }
}

.section {
  margin-bottom: 30px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.department-section {
  background-color: #f0f9ff;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid #3498db;
  
  .section-header {
    h3 {
      color: #3498db;
    }
  }
}

.department-content {
  text-align: center;
  padding: 15px 0;
  
  .department-tag {
    font-size: 18px;
    padding: 12px 24px;
    margin-bottom: 10px;
  }
  
  .department-tip {
    color: #7f8c8d;
    font-size: 14px;
    margin-top: 10px;
  }
}

.custom-collapse {
  border: none;
  margin-top: 20px;
  
  :deep(.el-collapse-item__header) {
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
    padding-left: 10px;
    border-bottom: 1px solid #eee;
    height: 50px;
    line-height: 50px;
  }
  
  :deep(.el-collapse-item__content) {
    padding-bottom: 0;
  }
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  
  h3 {
    font-size: 20px;
    font-weight: 600;
    color: #3498db;
    margin: 0;
  }
  
  .section-icon {
    width: 24px;
    height: 24px;
    color: #3498db;
  }
}

.custom-divider {
  margin: 10px 0 15px;
  
  :deep(.el-divider__text) {
    background-color: transparent;
    padding: 0 10px;
    color: #95a5a6;
    font-size: 12px;
  }
}

.content-box {
  white-space: pre-wrap;
  font-family: 'Roboto', 'PingFang SC', sans-serif;
  line-height: 1.8;
  color: #34495e;
  margin: 0;
  padding: 15px;
  background-color: #f8fafc;
  border-radius: 8px;
  font-size: 15px;
}

.no-result {
  text-align: center;
  padding: 40px 0;
  
  .el-empty {
    :deep(.el-empty__description) {
      p {
        color: #7f8c8d;
        font-size: 16px;
      }
    }
  }
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
  
  @media (max-width: 600px) {
    flex-direction: column;
    gap: 12px;
  }
}

.action-btn {
  padding: 12px 28px;
  font-size: 16px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(52, 152, 219, 0.2);
  }
  
  .btn-icon {
    width: 18px;
    height: 18px;
  }
}

.el-button--primary {
  background-color: #3498db;
  border-color: #3498db;
  
  &:hover {
    background-color: #2980b9;
    border-color: #2980b9;
  }
}

.el-button--success {
  background-color: #2ecc71;
  border-color: #2ecc71;
  
  &:hover {
    background-color: #27ae60;
    border-color: #27ae60;
  }
}
</style>