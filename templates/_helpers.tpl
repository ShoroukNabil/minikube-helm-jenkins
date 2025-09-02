{{/* Chart and naming helpers for mvn-stack */}}

{{- define "mvn-stack.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{- define "mvn-stack.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end }}
{{- end }}

{{- define "mvn-stack.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" -}}
{{- end }}

{{- define "mvn-stack.labels" -}}
helm.sh/chart: {{ include "mvn-stack.chart" . }}
app.kubernetes.io/name: {{ include "mvn-stack.name" . }}
app.kubernetes.io/instance: {{ .Release.Name | quote }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service | quote }}
{{- end }}

{{- define "mvn-stack.selectorLabels" -}}
app.kubernetes.io/name: {{ include "mvn-stack.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{- define "mvn-stack.mysqlFullname" -}}
{{- printf "%s-mysql" (include "mvn-stack.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{- define "mvn-stack.mysqlSecretName" -}}
{{- printf "%s-mysql-secret" (include "mvn-stack.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{- define "mvn-stack.backendFullname" -}}
{{- printf "%s-backend" (include "mvn-stack.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{- define "mvn-stack.frontendFullname" -}}
{{- printf "%s-frontend" (include "mvn-stack.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end }}
