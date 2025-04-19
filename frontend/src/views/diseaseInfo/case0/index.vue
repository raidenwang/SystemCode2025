<template>
  <div class="condition-container">
    <div class="content-card">
      <h1 class="main-title">常见临床病症介绍</h1>

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
    title: '头疼',
    description: '头痛是人们常见的健康问题之一。头痛的原因可以多种多样，包括紧张性头痛、偏头痛、颈部和头部肌肉张力不良、颅内压增高等。除了生活压力和劳累外，其他潜在原因可能是饮食、环境、药物和遗传因素。'
  },
  {
    title: '发烧',
    description: '发烧是机体对感染、炎症或其他疾病的一种自然反应。它可能由细菌、病毒、真菌或其他微生物引起。发烧还可能是免疫系统应对某些情况的一种方式，例如发生过敏反应。'
  },
  {
    title: '咳嗽',
    description: '咳嗽是身体的一种防御机制，用来清除气道中的异物或分泌物。病因包括感染（如感冒、流感）、支气管炎、哮喘、慢性阻塞性肺部疾病（COPD），甚至心脏问题。'
  },
  {
    title: '腹泻',
    description: '腹泻是指排便次数明显增多，粪便量减少，质地稀薄，甚至液状，并伴有腹痛、腹胀等症状。最常见的原因包括感染（细菌、病毒或寄生虫）、食物中毒、食物不耐受、压力等。'
  },
  {
    title: '肌肉酸痛',
    description: '肌肉酸痛可能是由于长时间的过度运动、肌肉损伤或其他炎症性疾病所致。例如，病毒性感染、纤维肌痛综合征、类风湿关节炎等。'
  },
  {
    title: '皮疹',
    description: '皮疹是指身体上出现的小斑块、丘疹、水疱或结节疹。皮疹常见的原因包括过敏反应、感染性疾病（例如麻疹、水痘）、药物反应、接触性皮炎等。'
  },
  {
    title: '恶心与呕吐',
    description: '恶心与呕吐可以是许多健康问题的症状，例如食物中毒、感染、晕动病，也可以是怀孕、化疗药物、放射疗法引起的副作用。'
  }
];
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