import { defineStore } from 'pinia'
import { IApiBasicConfigEditItem,getIPAndPort,parseParam,executeSql,IParamItem,IApiSqlItem,parseDynamicSql,IParseDynamicSqlResult } from '/@/api/api/api'

export interface SqlState {
  sqlList: IApiSqlItem[]
}

export const useSqlStore = defineStore({
  id: 'sql',
  state: ():any => ({
    sqlList: []
  }),
  getters: {
    getSqlList():IApiSqlItem[] {
      return this.sqlList
    }
  },
  actions: {
    getSqlListByIndex(index:number):IApiSqlItem[] {
      if(index) {
        return this.sqlList[index]
      }
      return this.sqlList
    },
    addSqlList(sqlItem:IApiSqlItem):void {
      this.sqlList.push(sqlItem)
    },
    addSqlItem():void {
      this.sqlList.push({
        id: `${(new Date()).getTime()}`,
        apiId: '',
        label: `${this.sqlList.length + 1}`,
        sqlText: '-- 请输入sql，一个标签只能输入一条sql',
        transformPlugin: '',
        transformPluginParams: '' 
      })
    },
    clearSqlList() {
      this.sqlList.splice(0)
    },
    resetSqlList(sqlList:IApiSqlItem[]) {
      this.clearSqlList()
      sqlList.forEach((item:IApiSqlItem,index:number) => {
        if(!item.id) {
          item.id = `${(new Date()).getTime()}`
        }
        if(!item.label) {
          item.label = `${index + 1}`
        }
      })
      this.sqlList.push(...sqlList)
    },
    removeSqlList(id:string) {
      const index = this.sqlList.findIndex((item:IApiSqlItem) => item.id === id)
      this.sqlList.splice(index,1)
    },
    removeSqlListByIndex(index:number) {
      this.sqlList.splice(index,1)
    },
    resetSqlListLabel() {
      this.sqlList.forEach((item:IApiSqlItem,index:number) => {
        item.label = `${index + 1}`
      })
    }
  }
})