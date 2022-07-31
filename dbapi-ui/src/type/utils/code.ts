export interface GetEffectValueFunction {
  (type:string, value:string|number, expectValue?:string):string|number
}

export interface IValueItem {
  va:string
}

export interface IParamItem {
  name: string
  type: string
  value?: string
  values?: IValueItem[]
}

export interface IGenerateParam {
  lang: string
  address: string
  requestUrl: string
  isContentTypeJson: boolean
  isContentTypeFormUrlEncoded: boolean
  contentType: string
  isPrevilegePrivate: boolean
  isPrevilegePublic: boolean
  token: string
  params: IParamItem[]
  detail:IDetail
  getEffectValue: GetEffectValueFunction
}
  
export interface IDetail {
  contentType: string
  previlege: number
  token: string
  params: IParamItem[]
  jsonParam: string
}
  
export interface IGenerateCallExampleCodeParam {
  lang:string
  address:string
  detail:IDetail
}