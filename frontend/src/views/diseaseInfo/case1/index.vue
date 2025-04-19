<template>
    <div class="condition-container">
      <div class="content-card">
        <h1 class="main-title">疼痛症状的常见病症</h1>
        
        <div class="condition-list">
          <div v-for="(condition, index) in conditions" :key="index" class="condition-item">
            <h3 class="condition-title">{{ condition.title }}</h3>
            <p class="condition-description">{{ condition.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  const conditions = [
    {
      title: '慢性疼痛症状',
      description: '这类症状可能由多种疾病引起，例如类风湿关节炎、纤维肌痛综合征(FMS)、慢性头痛等。类风湿关节炎导致关节发炎和疼痛，而FMS则表现为全身性长期疼痛和压痛点。慢性头痛可能是由各种原因引起的头部疼痛，如张力性头痛和偏头痛。'
    },
    {
      title: '骨折或骨裂',
      description: '骨折或骨裂通常在受伤后出现急剧和持续的疼痛，伴随着局部隆起、肿胀和活动受限的症状。这种疼痛可以对应到具体的骨折部位。'
    },
    {
      title: '消化系统问题',
      description: '胃溃疡、胰腺炎和胆结石等消化系统问题可能导致腹部疼痛。胃溃疡通常伴随着胃部灼热感和消化不良，胰腺炎可引起剧烈的上腹疼痛，胆结石通常导致阵发性胆囊区及右上腹绞痛。'
    },
    {
      title: '神经系统疾病',
      description: '坐骨神经痛表现为臀部、腿部的尖锐、剧烈疼痛，常伴有麻木刺痛感。带状疱疹后遗症疼痛则是带状疱疹皮疹消退后持续存在或反复发作的神经痛。'
    },
    {
      title: '心血管疾病',
      description: '心绞痛和心肌梗死可引起心脏疼痛。心绞痛通常表现为胸闷、憋气和紧迫感，心肌梗死则伴随着剧烈持续的胸痛、出汗和呼吸急促。'
    },
    {
      title: '癌症',
      description: '癌症可导致局部或广泛的疼痛症状，取决于癌细胞的位置和扩散程度。癌症疼痛可能与肿瘤压迫、侵蚀神经或引起炎症有关。'
    },
    {
      title: '头痛和偏头痛',
      description: '头痛和偏头痛可能由多种因素引起，包括压力、神经性疼痛等。此外，偏头痛还可能受到遗传、荷尔蒙水平变化、饮食等因素的影响。'
    }
  ]
  </script>
  
  <style lang="scss" scoped>
  @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@400;500;700&display=swap');
  
  $primary-color: #409EFF;
  $text-color: #333;
  $border-radius: 12px;
  $box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  
  .condition-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
    font-family: 'Noto Sans SC', sans-serif;
  }
  
  .content-card {
    background: white;
    border-radius: $border-radius;
    box-shadow: $box-shadow;
    padding: 2rem;
    transition: transform 0.2s;
  
    &:hover {
      transform: translateY(-4px);
    }
  }
  
  .main-title {
    color: $primary-color;
    text-align: center;
    margin-bottom: 2rem;
    position: relative;
  
    &::after {
      content: '';
      position: absolute;
      width: 60px;
      height: 3px;
      background: $primary-color;
      bottom: -10px;
      left: 50%;
      transform: translateX(-50%);
    }
  }
  
  .condition-list {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 1.5rem;
  }
  
  .condition-item {
    padding: 1.2rem;
    border-radius: $border-radius;
    background: #f8f9fa;
    transition: all 0.2s;
  
    &:hover {
      background: #f0f2f5;
      transform: translateX(8px);
    }
  }
  
  .condition-title {
    color: #2c3e50;
    margin-bottom: 0.8rem;
    font-size: 1.25rem;
    position: relative;
  
    &::before {
      content: '';
      position: absolute;
      width: 6px;
      height: 6px;
      background: $primary-color;
      border-radius: 50%;
      left: -12px;
      top: 50%;
      transform: translateY(-50%);
    }
  }
  
  .condition-description {
    color: $text-color;
    line-height: 1.6;
    margin: 0;
  }
  
  @media (max-width: 768px) {
    .condition-list {
      grid-template-columns: 1fr;
    }
  
    .content-card {
      padding: 1.5rem;
    }
  }
  </style>