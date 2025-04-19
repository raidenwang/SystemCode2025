<template>
    <div class="container">
      <!-- 左侧：Three.js 模型区域 -->
      <div class="left-panel">
        <div id="three-container"></div>
        
        <!-- 加载状态 -->
        <!-- <div v-if="loading" class="loading-overlay">
          <div class="loader"></div>
          <p>正在加载模型...</p>
        </div> -->
  
        <!-- 弹窗 -->
        <div v-if="popupVisible" class="popup">
          <p>{{ popupContent }}</p>
          <div class="action-buttons">
            <button class="confirm-btn" @click="handleConfirm">确定</button>
            <button class="cancel-btn" @click="popupVisible = false">取消</button>
          </div>
        </div>
      </div>
  
      <!-- 右侧：疾病症状选择面板 -->
      <div class="right-panel" v-if="showDiseaseSelector">
        <DiseaseSymptomSelector
          :diseasePart="diseasePart"
          :availableOptions="availableOptions"
          :loading="optionsLoading"
          @hide="hideDiseaseSelector"
        />
      </div>
    </div>
  </template>
  
  <script>
  import * as THREE from "three";
  import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader.js";
  import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";
  import DiseaseSymptomSelector from "../diseaseSymptomSelector/index.vue"; // 引入疾病选择组件
  import { symptomConfig } from "../../../types/disease";
  import { getAllCategories } from "../../../api/disease/disease";
  const formData = ref({
    name: "",
    age: "",
    weight: "",
    gender: null,
    ...JSON.parse(localStorage.getItem("patientInfo") || {}),
  });
  export default {
    name: "ThreeModel",
    components: { DiseaseSymptomSelector },
    data() {
      return {
        loading: true,
        popupVisible: false,
        popupContent: '',
        clickedPart: '',
        showDiseaseSelector: false,
        diseasePart: '',
        availableOptions: {},
        optionsLoading: false,
        formData: JSON.parse(localStorage.getItem("patientInfo") || "{}")
      };
    },
    mounted() {
      const container = document.getElementById("three-container");
      const scene = new THREE.Scene();
      const camera = new THREE.PerspectiveCamera(
        45,
        container.offsetWidth / container.offsetHeight,
        0.1,
        1000
      );
      const renderer = new THREE.WebGLRenderer({ antialias: true });
      renderer.setPixelRatio(window.devicePixelRatio * 2);
      renderer.setSize(container.offsetWidth, container.offsetHeight);
      renderer.setClearColor(0xffffff);
      container.appendChild(renderer.domElement);
  
      // 添加光源
      const ambientLight = new THREE.AmbientLight(0xffffff, 1.5);
      scene.add(ambientLight);
      const pointLight = new THREE.PointLight(0xffffff, 2);
      pointLight.position.set(20, 20, 20);
      scene.add(pointLight);
  
      const loader = new GLTFLoader();
      const age = parseInt(formData.value.age, 10);
      const gender = parseInt(formData.value.gender, 10);
      const modelPath = age < 18 ? '/child.glb' : gender === 1 ? '/male.glb' : '/female.glb';
      console.log(modelPath)
      loader.load(
        modelPath,
        (gltf) => {
          const model = gltf.scene;
          scene.add(model);
  
          // 计算模型的包围盒
          const box = new THREE.Box3().setFromObject(model);
          const center = box.getCenter(new THREE.Vector3());
          const size = box.getSize(new THREE.Vector3());
  
          // 设置模型缩放与位置
          model.scale.set(2.5, 2.5, 2.5);
          model.position.set(0, -center.y * 2.5, 0);
  
          // 设置相机位置
          const maxDim = Math.max(size.x, size.y, size.z);
          camera.position.set(0, 0, maxDim * 3.5);
          camera.lookAt(center);
          camera.near = maxDim / 100;
          camera.far = maxDim * 10;
          camera.updateProjectionMatrix();
  
          // 添加点击事件监听
          const raycaster = new THREE.Raycaster();
          const mouse = new THREE.Vector2();
          const onMouseClick = (event) => {
            const rect = renderer.domElement.getBoundingClientRect();
            mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
            mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
  
            raycaster.setFromCamera(mouse, camera);
            // 设置 recursive 为 true ，遍历模型所有子对象
            const intersects = raycaster.intersectObject(model, true);
            if (intersects.length > 0) {
              const clickedObject = intersects[0].object;
              this.showPopup(clickedObject.name);
            }
          };
          renderer.domElement.addEventListener("click", onMouseClick);
  
          // 动画循环
          const animate = () => {
            requestAnimationFrame(animate);
            renderer.render(scene, camera);
          };
          animate();
        },
        undefined,
        (error) => {
          console.error("模型加载错误:", error);
        }
      );
  
      // 添加 OrbitControls
      const controls = new OrbitControls(camera, renderer.domElement);
      controls.enableDamping = true;
      controls.dampingFactor = 0.25;
    },
    methods: {
      handleModelClick(event) {
        // ...原有点击检测逻辑...
        this.showPopup(clickedObject.name)
      },
      // 根据点击的部位显示不同的提示内容
      showPopup(partName) {
        this.clickedPart = partName;
        switch (partName) {
          case "Wolf3D_Head":
            this.popupContent = "是否进入五官的病状选择？";
            break;
          case "Wolf3D_Body":
            this.popupContent = "是否进入手臂的病状选择？";
            break;
          case "Wolf3D_Outfit_Bottom":
            this.popupContent = "是否进入腿部的病状选择？";
            break;
          case "Wolf3D_Outfit_Footwear":
            this.popupContent = "是否进入足部的病状选择？";
            break;
          case "Wolf3D_Hair":
            this.popupContent = "是否进入头部的病状选择？";
            break;
          case "Wolf3D_Outfit_Top":
            this.popupContent = "是否进入躯干的病状选择？";
            break;
          case "EyeLeft"||"EyeRight":
            this.popupContent = "是否进入五官的病状选择？";
            break;  
          default:
            this.popupContent = `是否进入 ${partName} 的病状选择？`;
        }
        this.popupVisible = true;
      },
      // 点击弹窗确定后
      async handleConfirm() {
        this.popupVisible = false
        this.optionsLoading = true
        
        try {
          const { data } = await getAllCategories(this.clickedPart)//axios.get(`/symptoms/${this.clickedPart}`)
          
          this.diseasePart = data.diseasePart
          this.availableOptions = {
            ...data.availableOptions,
            其他: ["其他（手动输入）"]
          }
          this.showDiseaseSelector = true
        } catch (error) {
          console.error('症状加载失败:', error)
          this.$notify.error(error.response?.data?.message || '症状信息加载失败')
          this.showDefaultOptions()
        } finally {
          this.optionsLoading = false
        }
      },
      showDefaultOptions() {
        this.diseasePart = "自定义部位"
        this.availableOptions = { 其他: ["其他（手动输入）"] }
        this.showDiseaseSelector = true
      },
      // 隐藏疾病症状选择面板
      hideDiseaseSelector() {
        this.showDiseaseSelector = false;
      },
      // 显示疾病症状选择面板
      showDiseaseSelectorPanel() {
        this.showDiseaseSelector = true;
      },
    }
  };
  </script>
  
  <style scoped>
  .loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.9);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    z-index: 999;
  }
  
  .loader {
    border: 4px solid #f3f3f3;
    border-top: 4px solid #3498db;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  /* 整体布局：左右两个面板 */
  .container {
    display: flex;
    flex-direction: row;
  }
  /* 左侧面板：固定宽度，与 three.js 容器一致 */
  .left-panel {
    width: 500px;
    height: 500px;
    margin: 50px 0 0 20px;
    position: relative;
  }
  /* three.js 容器占满 left-panel */
  #three-container {
    width: 100%;
    height: 100%;
  }
  /* 弹窗样式，绝对定位于 left-panel 内部 */
  .popup {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: white;
    padding: 20px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
    border-radius: 8px;
    z-index: 10;
  }
  .popup button {
    margin: 5px;
    padding: 8px 12px;
  }
  /* 右侧面板：自适应宽度，可根据需要调整 */
  .right-panel {
    flex-grow: 1;
    background-color: #f5f5f5;
    padding: 20px;
    margin-left: 20px;
    min-width: 300px;
  }
  .action-buttons {
    display: flex;
    gap: 10px;
    margin-top: 20px;
  }
  
  .confirm-btn {
      background: #27ae60;
      color: white;
      border: none;
      padding: 6px 12px;
      border-radius: 4px;
      cursor: pointer;
    }
    
    .cancel-btn {
      background: #e74c3c;
      color: white;
      border: none;
      padding: 6px 12px;
      border-radius: 4px;
      cursor: pointer;
    }
  
  
  .btn {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.3s ease;
  }
  
  .save-btn {
    background-color: #4caf50;
    color: white;
  }
  
  .save-btn:hover {
    background-color: darken(#4caf50, 10%);
  }
  
  .cancel-btn {
    background-color: #f44336;
    color: white;
  }
  
  .cancel-btn:hover {
    background-color: darken(#f44336, 10%);
  }
  </style>