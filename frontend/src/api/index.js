import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000
})

// 请求拦截：自动附带 token
api.interceptors.request.use(config => {
  const token = sessionStorage.getItem('token')
  if (token) {
    config.headers['X-Auth-Token'] = token
  }
  return config
})

// 响应拦截：401 跳转登录
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      sessionStorage.clear()
      ElMessage.error('登录已过期，请重新登录')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default {
  // Auth
  login(data) { return api.post('/auth/login', data) },
  register(data) { return api.post('/auth/register', data) },

  // Dashboard
  getDashboard() { return api.get('/dashboard') },

  // Users
  getUsers() { return api.get('/users') },
  searchUsers(keyword, type) { return api.get('/users/search', { params: { keyword, type } }) },
  addUser(data) { return api.post('/users', data) },
  updateUser(username, data) { return api.put(`/users/${username}`, data) },
  deleteUser(username) { return api.delete(`/users/${username}`) },

  // Elderly
  getElderly(keyword) { return api.get('/elderly', { params: { keyword } }) },
  getElderlyById(id) { return api.get(`/elderly/${id}`) },
  checkin(data) { return api.post('/elderly/checkin', data) },
  setNursingLevel(id, data) { return api.put(`/elderly/${id}/nursing-level`, data) },

  // Nursing
  getLevels() { return api.get('/nursing/levels') },
  addLevel(data) { return api.post('/nursing/levels', data) },
  getProjects() { return api.get('/nursing/projects') },
  getApplicableProjects(levelCode) { return api.get(`/nursing/projects/applicable/${levelCode}`) },
  addProject(data) { return api.post('/nursing/projects', data) },
  getRecords() { return api.get('/nursing/records') },
  getNurseRecords(nurseName) { return api.get(`/nursing/records/nurse/${nurseName}`) },
  createRecord(data) { return api.post('/nursing/records', data) },

  // Beds
  getBuildings() { return api.get('/buildings') },
  getRooms() { return api.get('/rooms') },
  getBedDiagram() { return api.get('/beds/diagram') },
  getAvailableBeds() { return api.get('/beds/available') },
  addBed(data) { return api.post('/beds', data) },
  updateBed(id, data) { return api.put(`/beds/${id}`, data) },
  swapBed(data) { return api.put('/beds/swap', data) },

  // Out & Checkout
  getOutRegs(params) { return api.get('/out-registrations', { params }) },
  addOutReg(data) { return api.post('/out-registrations', data) },
  markReturn(id, data) { return api.put(`/out-registrations/${id}/return`, data) },
  markTimeout(id) { return api.put(`/out-registrations/${id}/timeout`) },
  getCheckouts(customerId) { return api.get('/checkouts', { params: { customerId } }) },
  createCheckout(data) { return api.post('/checkouts', data) },
  confirmCheckout(id) { return api.put(`/checkouts/${id}/confirm`) },
  revokeCheckout(id) { return api.delete(`/checkouts/${id}`) },

  // Employees
  getEmployees(params) { return api.get('/employees', { params }) },
  addEmployee(data) { return api.post('/employees', data) },
  updateEmployee(id, data) { return api.put(`/employees/${id}`, data) },
  deleteEmployee(id) { return api.delete(`/employees/${id}`) },

  // Services
  getServices(status) { return api.get('/services', { params: { status } }) },
  createService(data) { return api.post('/services', data) },
  followupService(id, data) { return api.put(`/services/${id}/followup`, data) },
  renewService(id, data) { return api.put(`/services/${id}/renew`, data) },
  revokeService(id) { return api.put(`/services/${id}/revoke`) },

  // Health
  getHealthRecords(elderlyId) { return api.get('/health-records', { params: { elderlyId } }) },
  addHealthRecord(data) { return api.post('/health-records', data) },

  // Logs
  getOperationLogs(keyword) { return api.get('/logs', { params: { keyword } }) },

  // Messages
  getMessages(receiverName) { return api.get('/messages', { params: { receiverName } }) },
  getUnreadCount(receiverName) { return api.get('/messages/unread-count', { params: { receiverName } }) },
  markMessageRead(id) { return api.put(`/messages/${id}/read`) },

  // Diet
  getFoods() { return api.get('/diet/foods') },
  addFood(data) { return api.post('/diet/foods', data) },
  updateFood(id, data) { return api.put(`/diet/foods/${id}`, data) },
  deleteFood(id) { return api.delete(`/diet/foods/${id}`) },
  getDietCalendar() { return api.get('/diet/calendar') },
  addDietCalendar(data) { return api.post('/diet/calendar', data) },
  updateDietCalendar(id, data) { return api.put(`/diet/calendar/${id}`, data) },
  deleteDietCalendar(id) { return api.delete(`/diet/calendar/${id}`) },
  getDietPreferences(elderlyId) { return api.get('/diet/preferences', { params: { elderlyId } }) },
  addDietPreference(data) { return api.post('/diet/preferences', data) },
  updateDietPreference(id, data) { return api.put(`/diet/preferences/${id}`, data) },
  deleteDietPreference(id) { return api.delete(`/diet/preferences/${id}`) },
}
