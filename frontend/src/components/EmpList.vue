<template>
  <div style="padding:24px; max-width:1000px; margin:0 auto">
    <el-card>
      <div slot="header" class="clearfix">
        <span>员工列表</span>
        <div style="float:right">
          <router-link to="/"><el-button size="small">返回新增页面</el-button></router-link>
          <el-button size="small" type="primary" @click="load" style="margin-left:8px">刷新</el-button>
        </div>
      </div>

      <el-table :data="emps" style="width: 100%" v-loading="loading">
        <el-table-column prop="eId" label="e_id" width="80" />
        <el-table-column prop="eName" label="e_name" />
        <el-table-column prop="eDId" label="e_d_id" width="100" />
        <el-table-column prop="eHireDate" label="e_hire_date" width="150" :formatter="formatDate" />
        <el-table-column prop="eSex" label="e_sex" width="100" />
      </el-table>

      <div v-if="error" style="color:#b00020; margin-top:12px">{{ error }}</div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: 'EmpList',
  data() {
    return { emps: [], error: '', loading: false };
  },
  created() {
    this.load();
  },
  methods: {
    async load() {
      this.error = '';
      this.loading = true;
      try {
        const res = await axios.get('/api/emps');
        this.emps = res.data;
      } catch (err) {
        console.error(err);
        this.error = (err.response && err.response.data) ? JSON.stringify(err.response.data) : err.message;
      } finally {
        this.loading = false;
      }
    },
    formatDate(row, column, cellValue) {
      if (!cellValue) return '';
      const s = String(cellValue);
      if (s.indexOf('T') !== -1) return s.split('T')[0];
      return s;
    }
  }
};
</script>

<style scoped>
.el-card { padding: 16px }
</style>
