import request from '../utils/request'

export default {
    findPage({ current, size, query }) {
        return request.get('/${table.entityPath}/pageData',
            {
                current,
                size,
                ...query
            })
    },
    get(id) {
        return request.get('/${table.entityPath}/get', { id })
    },
    add(data) {
        return request.post('/${table.entityPath}/save', data)
    },
    update(data) {
        return request.post('/${table.entityPath}/update', data)
    },
    delete(id) {
        return request.get('/${table.entityPath}/delete', { id })
    }
}