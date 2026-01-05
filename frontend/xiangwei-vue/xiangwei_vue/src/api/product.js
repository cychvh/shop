import request from '../utils/request'

export function getProductList() {
  return request.get('/product/list')
}

export function getProductDetail(id) {
  return request.get(`/product/${id}`)
}
