import Vue from 'vue';
import Router from 'vue-router';
import EmpForm from '../components/EmpForm.vue';
import EmpList from '../components/EmpList.vue';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    { path: '/', name: 'form', component: EmpForm },
    { path: '/emps', name: 'list', component: EmpList }
  ]
});
