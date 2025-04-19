<template>
  <div class="hospital-detail">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-spinner">
      <Spin tip="加载中..." size="large" />
    </div>

    <!-- 内容区域 -->
    <div v-else class="content">
      <!-- 医院信息 -->
      <Card class="hospital-info">
        <h2 class="hospital-name">{{ hospital.name }}</h2>
        <Divider />
        <p><strong>地址: </strong>{{ hospital.address }}</p>
        <p><strong>电话: </strong>{{ hospital.phone }}</p>
        <p><strong>信息: </strong>{{ hospital.info }}</p>
      </Card>

      <!-- 科室分类卡片 -->
      <h3 class="department-title">科室分类:</h3>
      <div class="category-container">
        <Card
          v-for="(departments, category) in groupedDepartments"
          :key="category"
          class="category-card"
          @click="showCategoryDepartments(category)"
          hoverable
        >
          <div class="category-card-content">
            <h4 class="category-name">{{ category }}</h4>
            <span class="department-count">{{ departments.length }} 个科室</span>
          </div>
        </Card>
      </div>

      <!-- 科室列表模态框 -->
      <Modal
        v-model:visible="categoryModalVisible"
        :title="currentCategory + ' - 科室列表'"
        @cancel="closeCategoryModal"
        width="600px"
        :footer="null"
      >
        <List
          :data-source="currentDepartments"
          bordered
          class="department-list"
        >
          <template #renderItem="{ item }">
            <ListItem
              class="department-item"
              @click="handleDepartmentClick(item.id)"
            >
              {{ item.name }}
              <template #actions>
                <span class="view-detail">查看详情 →</span>
              </template>
            </ListItem>
          </template>
        </List>
      </Modal>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Spin, Card, List, Modal, Divider, ListItem } from 'ant-design-vue';
import { getHospitalById, getDepartmentsByHospitalId } from "@/api/guide/guide";
import { getDepartments } from '../api/guide/guide';


const router = useRouter();

// 状态管理
const hospital = ref({ name: '', address: '', phone: '', info: '' });
const departments = ref([]);
const loading = ref(true);
const categoryModalVisible = ref(false);
const currentCategory = ref('');
const currentDepartments = ref([]);

// 获取医院信息
const fetchHospitalDetails = async () => {
  try {
    const response = await getHospitalById(1);
    console.log("Hospital response:", response);
    if (response.data && response.data.length > 0) {
      hospital.value = response.data[0]; // 取数组中的第一个元素
    } else {
      console.error('Unexpected hospital response structure:', response);
    }
  } catch (error) {
    console.error('Error fetching hospital details:', error);
  }
};

// 获取科室信息
const fetchDepartmentsDetails = async () => {
  try {
    //const response = await getDepartmentsByHospitalId(1);
    const response = await getDepartments();
    if (response.data) {
      departments.value = response.data;
    }
  } catch (error) {
    console.error('获取科室信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 显示分类科室模态框
const showCategoryDepartments = (category) => {
  console.log('当前分类:', category);
  console.log('分组数据:', groupedDepartments.value);
  currentCategory.value = category;
  currentDepartments.value = groupedDepartments.value[category];
  console.log('当前科室列表:', currentDepartments.value);
  categoryModalVisible.value = true;
};

// 关闭模态框
const closeCategoryModal = () => {
  categoryModalVisible.value = false;
  currentCategory.value = '';
  currentDepartments.value = [];
};

// 按科室分类
const groupedDepartments = computed(() => {
  return departments.value.reduce((acc, department) => {
    const { category } = department;
    acc[category] = acc[category] || [];
    acc[category].push(department);
    return acc;
  }, {});
});

// 处理科室点击事件
const handleDepartmentClick = (departmentId) => {
  router.push(`/departments/department?departmentId=${departmentId}`);
};

// 初始化加载
onMounted(() => {
  fetchHospitalDetails();
  fetchDepartmentsDetails();
});
</script>



<style scoped>
.hospital-detail {
  padding: 20px;
  background: linear-gradient(135deg, #f0f2f5, #e6f7ff);
  min-height: 100vh;
}

.loading-spinner {
  text-align: center;
  padding: 50px 0;
}

.content {
  max-width: 800px;
  margin: 0 auto;
}

.hospital-info {
  margin-bottom: 20px;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.hospital-name {
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
  margin-bottom: 16px;
}



.department-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}
.category-card {
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
  background-color: #fff;
}

.category-title {
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
  margin-bottom: 12px;
}

.department-list {
  max-height: 60vh;
  overflow-y: auto;
}

.department-item {
  padding: 12px 16px;
  transition: background-color 0.3s ease;
  cursor: pointer;
}

.department-item:hover {
  background-color: #f0f5ff;
}

.view-detail {
  color: #1890ff;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.department-item:hover .view-detail {
  opacity: 1;
}

.category-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.category-card {
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.category-card-content {
  padding: 16px;
}

.category-name {
  font-size: 18px;
  color: #1890ff;
  margin-bottom: 8px;
}

.department-count {
  font-size: 14px;
  color: #666;
}

.department-list {
  max-height: 60vh;
  overflow-y: auto;
}

.department-item {
  padding: 12px 16px;
  transition: background-color 0.3s ease;
  cursor: pointer;
}

.department-item:hover {
  background-color: #f0f5ff;
}

.view-detail {
  color: #1890ff;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.department-item:hover .view-detail {
  opacity: 1;
}
</style>