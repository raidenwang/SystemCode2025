<template>
    <div class="condition-container">
      <div class="content-card">
        <h1 class="main-title">腹痛的可能病因</h1>
        
        <div class="card-grid">
          <div class="primary-card">
            <p class="card-description">
              腹痛是人体腹部发生的一种常见不适感，它可能是由多种疾病和情况引起的症状之一。腹痛的常见病症包括：
            </p>
          </div>
  
          <div class="card-column">
            <div class="info-card">
              <h3 class="card-subtitle">消化系统疾病</h3>
              <p class="card-text">胃炎、胃溃疡、胰腺炎、胆囊炎、肠道感染、克罗恩病等</p>
            </div>
            <div class="info-card">
              <h3 class="card-subtitle">妇科疾病</h3>
              <p class="card-text">月经期间的痉挛性腹痛、子宫内膜异位症、卵巢囊肿、输卵管炎等</p>
            </div>
          </div>
  
          <div class="card-column">
            <div class="info-card">
              <h3 class="card-subtitle">泌尿系统疾病</h3>
              <p class="card-text">尿路感染、泌尿系统结石、肾脏疾病、腹膜炎等</p>
            </div>
            <div class="info-card">
              <h3 class="card-subtitle">其他因素</h3>
              <p class="card-text">心理因素（焦虑、抑郁）、神经系统疾病、血液循环问题、代谢性疾病等</p>
            </div>
          </div>
        </div>
      </div>
  
      <div class="content-card">
        <h1 class="main-title">关节痛的可能病因</h1>
        
        <div class="card-grid">
          <div class="primary-card">
            <p class="card-description">
              关节痛是指人体关节部位出现的疼痛症状，常见病症包括：
            </p>
          </div>
  
          <div class="card-column">
            <div class="info-card">
              <h3 class="card-subtitle">风湿性关节炎</h3>
              <p class="card-text">自身免疫性疾病，免疫系统攻击身体组织导致关节损伤</p>
            </div>
            <div class="info-card">
              <h3 class="card-subtitle">骨关节炎</h3>
              <p class="card-text">关节软骨老化退化导致的疼痛和活动受限</p>
            </div>
          </div>
  
          <div class="card-column">
            <div class="info-card">
              <h3 class="card-subtitle">类风湿关节炎</h3>
              <p class="card-text">慢性全身性炎症性疾病，表现为对称性多发关节肿胀</p>
            </div>
            <div class="info-card">
              <h3 class="card-subtitle">强直性脊柱炎</h3>
              <p class="card-text">脊柱和骨盆的慢性炎症导致僵硬和疼痛</p>
            </div>
          </div>
        </div>
      </div>
  
      <div class="disclaimer">
        提示：如果出现持续或严重的症状，建议及时就医并接受专业诊断和治疗。
      </div>
    </div>
  </template>
  
  <script setup>
  // 纯展示组件，无需额外逻辑
  </script>
  
  <style lang="scss" scoped>
  @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@400;500;700&display=swap');
  
  $primary-color: #409EFF;
  $card-bg: #ffffff;
  $card-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  $border-radius: 16px;
  
  .condition-container {
    max-width: 1280px;
    margin: 0 auto;
    padding: 2rem;
    font-family: 'Noto Sans SC', sans-serif;
  }
  
  .content-card {
    background: $card-bg;
    border-radius: $border-radius;
    box-shadow: $card-shadow;
    padding: 2rem;
    margin-bottom: 2rem;
    transition: transform 0.2s;
  
    &:hover {
      transform: translateY(-4px);
    }
  }
  
  .main-title {
    color: $primary-color;
    font-size: 2.5rem;
    margin-bottom: 1.5rem;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      width: 80px;
      height: 4px;
      background: $primary-color;
      bottom: -8px;
      left: 50%;
      transform: translateX(-50%);
    }
  }
  
  .card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
    margin-top: 1.5rem;
  }
  
  .primary-card {
    grid-column: span 2;
    background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
    color: white;
    padding: 2rem;
    
    .card-description {
      line-height: 1.6;
    }
  }
  
  .card-column {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .info-card {
    background: #f8f9fa;
    border-radius: $border-radius;
    padding: 1.5rem;
    transition: transform 0.2s;
    
    &:hover {
      transform: translateX(8px);
    }
  }
  
  .card-subtitle {
    color: #2c3e50;
    font-size: 1.25rem;
    margin-bottom: 0.5rem;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      width: 8px;
      height: 8px;
      background: $primary-color;
      border-radius: 50%;
      left: -16px;
      top: 50%;
      transform: translateY(-50%);
    }
  }
  
  .card-text {
    color: #606266;
    line-height: 1.6;
    margin: 0;
  }
  
  .disclaimer {
    text-align: center;
    color: #909399;
    margin-top: 3rem;
    font-style: italic;
  }
  
  @media (max-width: 768px) {
    .card-grid {
      grid-template-columns: 1fr;
    }
    
    .primary-card {
      grid-column: span 1;
    }
  }
  </style>