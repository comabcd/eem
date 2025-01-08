<template>
  <div class="dashboard-container">
    <!-- 统计卡片行 -->
    <el-row :gutter="24">
      <el-col :span="6" v-for="(item, index) in statistics" :key="index">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon class="stat-icon" :class="item.type">
              <component :is="item.icon" />
            </el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表行 -->
    <el-row :gutter="24" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">部门人员分布</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="deptDistributionRef" style="height: 350px; margin-top: -20px"></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">入职趋势分析</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="onboardTrendRef" style="height: 400px"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  User,
  OfficeBuilding,
  DocumentAdd,
  TrendCharts
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const deptDistributionRef = ref(null)
const onboardTrendRef = ref(null)
let deptDistributionChart = null
let onboardTrendChart = null

// 统计数据
const statistics = ref([
  { label: '总员工数', value: 0, icon: 'User', type: 'primary' },
  { label: '总部门数（实际业务部门）', value: 0, icon: 'OfficeBuilding', type: 'success' },
  { label: '本月入职', value: 0, icon: 'DocumentAdd', type: 'warning' },
  { label: '离职率', value: '0%', icon: 'TrendCharts', type: 'info' }
])

// 获取统计数据
const getStatistics = async () => {
  try {
    const res = await request.get('/hr/statistics')
    if (res.code === 200) {
      statistics.value[0].value = res.data.totalEmployees
      statistics.value[1].value = res.data.totalDepartments
      statistics.value[2].value = res.data.monthlyOnboard
      statistics.value[3].value = res.data.turnoverRate + '%'
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 初始化部门分布图表
const initDeptDistribution = async () => {
  try {
    const res = await request.get('/hr/dept/distribution')
    if (res.code === 200) {
      console.log('部门分布数据', res.data)
      deptDistributionChart = echarts.init(deptDistributionRef.value)
      deptDistributionChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: (params) => {
            return `${params.name}<br/>人数：${params.value}人`
          }
        },
        series: [
          {
            type: 'sunburst',
            data: res.data,
            radius: ['20%', '90%'],
            itemStyle: {
              borderRadius: 7,
              borderWidth: 2,
              borderColor: '#fff'
            },
            label: {
              show: true,
              position: 'inside',
              fontSize: 14,
              rotate: 'tangential',
              formatter: function (param) {
                const name = param.name.length > 6 ? param.name.substring(0, 6) + '..' : param.name;
                return param.value > 0 ? `${name}\n${param.value}人` : name;
              },
              minAngle: 15
            },
            levels: [
              {}, // 留空，使用默认配置
              {
                // 最内层
                r0: '20%',
                r: '45%',
                label: {
                  rotate: 'tangential',
                  fontSize: 14,
                  color: '#fff',
                  padding: 5,
                  align: 'center'
                }
              },
              {
                // 中间层
                r0: '45%',
                r: '65%',
                label: {
                  rotate: 'tangential',
                  fontSize: 12,
                  align: 'left',
                  padding: [0, 0, 0, 10]
                }
              },
              {
                // 最外层
                r0: '65%',
                r: '90%',
                label: {
                  rotate: 'tangential',
                  fontSize: 12,
                  align: 'left',
                  padding: [0, 0, 0, 15]
                },
                itemStyle: {
                  borderWidth: 2
                }
              }
            ],
            emphasis: {
              focus: 'ancestor',
              label: {
                fontSize: 16,
                fontWeight: 'bold'
              }
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error('获取部门分布数据失败:', error)
  }
}

// 初始化入职趋势图表
const initOnboardTrend = async () => {
  try {
    const res = await request.get('/hr/onboard/trend')
    if (res.code === 200) {
      const { dates, counts } = res.data
      onboardTrendChart = echarts.init(onboardTrendRef.value)
      onboardTrendChart.setOption({
        title: {
          text: '入职趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: counts,
            type: 'line',
            smooth: true,
            lineStyle: {
              color: '#409EFF'
            },
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(64,158,255,0.3)'
                },
                {
                  offset: 1,
                  color: 'rgba(64,158,255,0.1)'
                }
              ])
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error('获取入职趋势数据失败:', error)
  }
}

// 监听窗口大小变化
const handleResize = () => {
  deptDistributionChart?.resize()
  onboardTrendChart?.resize()
}

onMounted(() => {
  getStatistics()
  initDeptDistribution()
  initOnboardTrend()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  deptDistributionChart?.dispose()
  onboardTrendChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

/* 统计卡片样式 */
.stat-card {
  margin-bottom: 24px;
  border-radius: 8px;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 16px;
}

.stat-icon {
  font-size: 52px;
  margin-right: 24px;
  padding: 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-icon.primary {
  color: #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
}

.stat-icon.success {
  color: #67C23A;
  background-color: rgba(103, 194, 58, 0.1);
}

.stat-icon.warning {
  color: #E6A23C;
  background-color: rgba(230, 162, 60, 0.1);
}

.stat-icon.info {
  color: #909399;
  background-color: rgba(144, 147, 153, 0.1);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 图表区域样式 */
.chart-row {
  margin-top: 0;  /* 移除额外的顶部间距 */
}

.chart-card {
  height: 100%;
  border-radius: 8px;
  transition: all 0.3s;
  margin-bottom: 0;  /* 移除底部间距 */
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  padding: 12px 24px 24px;
}

/* 响应式调整 */
@media screen and (max-width: 1400px) {
  .stat-icon {
    font-size: 44px;
    padding: 12px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .chart-container {
    padding: 10px 20px 20px;
  }
}

@media screen and (max-width: 1200px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .stat-content {
    padding: 12px;
  }
  
  .stat-icon {
    margin-right: 16px;
  }
  
  .chart-container {
    padding: 8px 16px 16px;
  }
}
</style> 