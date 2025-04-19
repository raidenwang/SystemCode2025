<template>
  <div class="app-container">
    <div class="form-wrapper">
      <div class="form-header">
        <div class="hospital-icon">
          <svg viewBox="0 0 24 24" width="36" height="36">
            <path fill="#409EFF" d="M19 11H13V5a1 1 0 0 0-1-1h-1a1 1 0 0 0-1 1v6H5a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1h6v6a1 1 0 0 0 1 1h1a1 1 0 0 0 1-1v-6h6a1 1 0 0 0 1-1v-1a1 1 0 0 0-1-1z"/>
          </svg>
        </div>
        <h2>患者信息确认</h2>
        <p class="form-subtitle">请核对您的个人信息</p>
      </div>
      
      <div class="form-container">
        <el-form
          class="patient-info-form"
          :model="patient"
          :rules="rules"
          @submit.prevent="handleSubmit"
          label-position="top"
        >
          <el-form-item label="姓名" prop="name">
            <el-input 
              v-model="patient.name" 
              placeholder="请输入姓名"
              size="large"
            >
              <template #prefix>
                <el-icon size="20"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="年龄" prop="age">
            <el-select 
              v-model="patient.age" 
              placeholder="请选择年龄" 
              size="large"
            >
              <el-option
                v-for="age in ageOptions"
                :key="age"
                :label="`${age}岁`"
                :value="age"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="体重" prop="weight">
            <el-select 
              v-model="patient.weight" 
              placeholder="请选择体重" 
              size="large"
            >
              <el-option
                v-for="weight in weightOptions"
                :key="weight"
                :label="`${weight}kg`"
                :value="weight"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="patient.gender">
              <el-radio-button :label="1">
                <el-icon size="18"><Male /></el-icon> 男
              </el-radio-button>
              <el-radio-button :label="0">
                <el-icon size="18"><Female /></el-icon> 女
              </el-radio-button>
            </el-radio-group>
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              native-type="submit"
              size="large"
              class="submit-btn"
            >
              确认信息并继续
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { User, Male, Female } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();

// 模拟从登录时获取的用户数据
const mockUserData = {
  name: '张三',
  age: 35,
  weight: 65,
  gender: 1
};

// 年龄和体重的可选范围
const ageOptions = Array.from({ length: 83 }, (_, i) => i + 18); // 18-100岁
const weightOptions = Array.from({ length: 81 }, (_, i) => i + 40); // 40-120kg

// 定义响应式对象和规则
const patient = reactive({
  name: mockUserData.name,
  age: mockUserData.age,
  weight: mockUserData.weight,
  gender: mockUserData.gender,
});

// 提交函数
const handleSubmit = async () => {
  // 将患者信息保存到 localStorage
  const patientInfo = {
    name: patient.name,
    age: patient.age,
    weight: patient.weight,
    gender: patient.gender,
  };
  localStorage.setItem('patientInfo', JSON.stringify(patientInfo));
  router.push(`${route.path}/human`);
};
</script>

<style scoped>
.app-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f7ff 0%, #e6f7ff 100%);
  padding: 20px;
}

.form-wrapper {
  width: 100%;
  max-width: 560px;
  background: white;
  border-radius: 18px;
  box-shadow: 0 12px 36px rgba(0, 120, 255, 0.15);
  overflow: hidden;
}

.form-header {
  background: linear-gradient(135deg, #409EFF 0%, #3375b9 100%);
  color: white;
  padding: 32px 36px;
  text-align: center;
  position: relative;
}

.form-header h2 {
  margin: 16px 0 10px;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.form-subtitle {
  opacity: 0.9;
  margin: 0;
  font-size: 18px;
  letter-spacing: 0.3px;
}

.hospital-icon {
  background: white;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
}

.form-container {
  padding: 36px 40px;
}

.patient-info-form {
  margin-top: 16px;
}

.el-form-item {
  margin-bottom: 28px;
}

.el-form-item__label {
  font-weight: 500;
  color: #606266;
  padding-bottom: 8px;
  font-size: 18px;
  margin-bottom: 8px;
}

.submit-btn {
  width: 100%;
  margin-top: 20px;
  height: 52px;
  font-size: 18px;
  font-weight: 500;
  letter-spacing: 0.8px;
  background: linear-gradient(135deg, #409EFF 0%, #3375b9 100%);
  border: none;
  transition: all 0.3s ease;
  border-radius: 12px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(64, 158, 255, 0.35);
}

.el-radio-button {
  display: flex;
  align-items: center;
}

.el-radio-button :deep(.el-radio-button__inner) {
  padding: 12px 24px;
  font-size: 16px;
}

.el-icon {
  vertical-align: middle;
  margin-right: 8px;
}

.el-input :deep(.el-input__prefix) {
  display: flex;
  align-items: center;
  padding-left: 14px;
}

.el-select, .el-input {
  width: 100%;
  font-size: 16px;
}

.el-input :deep(.el-input__inner),
.el-select :deep(.el-input__inner) {
  height: 48px;
  line-height: 48px;
  font-size: 16px;
  padding-left: 40px;
}

.el-input :deep(.el-input__prefix) {
  height: 48px;
  line-height: 48px;
}

.el-select :deep(.el-input__suffix) {
  height: 48px;
  line-height: 48px;
}

.el-select-dropdown__item {
  font-size: 16px;
  padding: 12px 20px;
}
</style>