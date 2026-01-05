import request from '../utils/request'

export function createOrder(items) {
  return request.post('/order/create', items)
}

export function getOrderList() {
  return request.get('/order/list')
}
