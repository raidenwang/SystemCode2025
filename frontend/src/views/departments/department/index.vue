<template>
  <div class="department-detail">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-spinner">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 内容区域 -->
    <div v-else class="content">
      <!-- 科室详细信息 -->
      <el-card class="department-info" shadow="hover">
        <h2 class="department-name">{{ departmentDetail?.name }}</h2>
        <el-divider />
        <p><strong>分类: </strong>{{ departmentDetail?.category }}</p>
        <p><strong>描述: </strong></p>
        <div
          class="department-description"
          v-html="formatDescription(departmentDetail?.info)"
        ></div>
        <el-divider />
      </el-card>

      <!-- 医生列表 -->
      <h3 class="doctor-title">医生列表</h3>
      <el-table
        :data="paginatedDoctors"
        style="width: 100%"
        @row-click="handleRowClick"
        stripe
        highlight-current-row
      >
        <el-table-column prop="imageUrl" label="医生头像" width="120">
          <template #default="{ row }">
            <el-avatar :src="row.imageUrl" :size="80" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column prop="title" label="头衔" width="150" />
        <el-table-column prop="info" label="信息" />
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="pagination"
        background
        layout="prev, pager, next"
        :total="doctors.length"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
      />

      <!-- 医生详细信息模态框 -->
      <el-dialog
        v-model="isModalVisible"
        :title="selectedDoctor ? `${selectedDoctor.name} 的详细信息` : ''"
        @close="handleCloseModal"
        width="600px"
      >
        <div v-if="selectedDoctor">
          <el-avatar :src="selectedDoctor.imageUrl" :size="100" style="margin-bottom: 16px" />
          <p><strong>姓名: </strong>{{ selectedDoctor.name }}</p>
          <p><strong>头衔: </strong>{{ selectedDoctor.title }}</p>
          <p><strong>信息: </strong>{{ selectedDoctor.info }}</p>
          <el-button
            type="primary"
            @click="fetchAvailability(selectedDoctor.id)"
            :loading="loadingAvailability"
            style="margin-top: 16px"
          >
            加载出诊时间
          </el-button>

          <!-- 出诊时间列表 -->
          <el-card v-if="availability.length > 0" style="margin-top: 16px">
            <template #header>
              <strong>出诊时间</strong>
            </template>
            <div v-for="item in availability" :key="item.id" style="margin-bottom: 8px">
              {{ item.day }} - {{ item.time }}
            </div>
          </el-card>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import {
  ElCard,
  ElDivider,
  ElTable,
  ElTableColumn,
  ElAvatar,
  ElDialog,
  ElButton,
  ElMessage,
  ElSkeleton,
  ElPagination,
} from 'element-plus';
import { getDepartmentByDeptId, getDepartmentById, getDoctorAvailability, getDoctors } from '../../../api/guide/guide';

const route = useRoute();

// 状态管理
const departmentDetail = ref(null);
const doctors = ref([]);
const loading = ref(true);
const selectedDoctor = ref(null);
const isModalVisible = ref(false);
const availability = ref([]);
const loadingAvailability = ref(false);

// 分页状态
const currentPage = ref(1);
const pageSize = ref(5);

// 从 URL 中获取 departmentId
const departmentId = route.query.departmentId;

// 获取科室详细信息
const fetchDepartmentDetail = async () => {
  try {
    //const departmentResponse = await getDepartmentById(departmentId);
    const departmentResponse = await getDepartmentByDeptId(departmentId);
    console.log(departmentResponse);
    departmentDetail.value = departmentResponse.data;

    const doctorsResponse = await getDoctors(departmentId);
    doctors.value = doctorsResponse.data;
  } catch (error) {
    console.error('Error fetching department details or doctors:', error);
    ElMessage.error('加载科室或医生信息失败，请稍后再试');
  } finally {
    loading.value = false;
  }
};

// 格式化科室描述
const formatDescription = (info) => {
  if (!info) return '';
  return info.split('\n').map(paragraph => `<p style="text-indent: 2em; margin-bottom: 10px;">${paragraph}</p>`).join('');
};

// 点击医生行显示详细信息模态框
const handleRowClick = (record) => {
  selectedDoctor.value = record;
  isModalVisible.value = true;
  availability.value = []; // 重置出诊时间
};

// 请求医生的出诊时间
const fetchAvailability = async (doctorId) => {
  loadingAvailability.value = true;
  try {
    const response = await getDoctorAvailability(doctorId);
    availability.value = response.data;
  } catch (error) {
    console.error('Error fetching availability:', error);
    ElMessage.error('加载出诊时间失败，请稍后再试');
  } finally {
    loadingAvailability.value = false;
  }
};

// 关闭模态框
const handleCloseModal = () => {
  isModalVisible.value = false;
  selectedDoctor.value = null;
  availability.value = [];
};

// 分页数据
const paginatedDoctors = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return doctors.value.slice(start, end);
});

// 分页切换
const handlePageChange = (page) => {
  currentPage.value = page;
};

// 初始化加载
onMounted(() => {
  fetchDepartmentDetail();
});
</script>

<style scoped>
.department-detail {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.loading-spinner {
  text-align: center;
  padding: 50px 0;
}

.content {
  max-width: 1200px;
  margin: 0 auto;
}

.department-info {
  margin-bottom: 20px;
  padding: 20px;
  border-radius: 8px;
  background-color: #ffffff;
}

.department-name {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.department-description {
  font-size: 16px;
  line-height: 28px;
  font-family: Arial, '微软雅黑';
  text-align: left;
}

.doctor-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #333;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.el-table {
  margin-top: 20px;
}

.el-dialog {
  border-radius: 8px;
}

.el-card {
  border-radius: 8px;
}
</style>