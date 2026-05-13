<script setup>
import { reactive, ref } from 'vue'
import { enviarMensajeContacto, solicitarColaboracion } from '../services/contactoService'

const contactForm = reactive({
	nombre: '',
	email: '',
	asunto: '',
	tipo: '',
	mensaje: '',
})

const collaboratorForm = reactive({
	nombreComercio: '',
	nombreTitular: '',
	emailComercio: '',
	telefonoComercio: '',
	categoria: '',
	descripcion: '',
	terminos: false,
})

const contactResponse = ref('')
const collaboratorResponse = ref('')
const isContactSubmitting = ref(false)
const isCollaboratorSubmitting = ref(false)
const contactError = ref('')
const collaboratorError = ref('')

function resetContactResponse() {
	contactResponse.value = ''
	contactError.value = ''
}

function resetCollaboratorResponse() {
	collaboratorResponse.value = ''
	collaboratorError.value = ''
}

async function handleContactSubmit() {
	isContactSubmitting.value = true
	contactError.value = ''
	try {
		const response = await enviarMensajeContacto({ ...contactForm })
		contactResponse.value = response?.message || '¡Gracias por tu mensaje! Nos pondremos en contacto pronto.'
		contactForm.nombre = ''
		contactForm.email = ''
		contactForm.asunto = ''
		contactForm.tipo = ''
		contactForm.mensaje = ''
	} catch (error) {
		const apiMessage = error?.response?.data?.details?.[0] || error?.response?.data?.message
		contactError.value = apiMessage || 'No se pudo enviar el mensaje. Inténtalo de nuevo.'
	} finally {
		isContactSubmitting.value = false
	}
}

async function handleCollaboratorSubmit() {
	isCollaboratorSubmitting.value = true
	collaboratorError.value = ''
	try {
		const response = await solicitarColaboracion({ ...collaboratorForm })
		collaboratorResponse.value = response?.message || '¡Gracias por tu solicitud! Nos pondremos en contacto en breve.'
		collaboratorForm.nombreComercio = ''
		collaboratorForm.nombreTitular = ''
		collaboratorForm.emailComercio = ''
		collaboratorForm.telefonoComercio = ''
		collaboratorForm.categoria = ''
		collaboratorForm.descripcion = ''
		collaboratorForm.terminos = false
	} catch (error) {
		const apiMessage = error?.response?.data?.details?.[0] || error?.response?.data?.message
		collaboratorError.value = apiMessage || 'No se pudo enviar la solicitud. Inténtalo de nuevo.'
	} finally {
		isCollaboratorSubmitting.value = false
	}
}
</script>

<template>
	<section class="contact-hero py-5 text-white">
		<div class="container py-4 py-lg-5">
			<div class="row align-items-center g-4">
				<div class="col-lg-8">
					<span class="badge rounded-pill text-bg-light text-primary px-3 py-2 mb-3">
						Contacto
					</span>
					<h1 class="display-5 fw-bold mb-3 text-white">Hablemos de tu barrio.</h1>
					<p class="lead mb-0 text-white-75">
						¿Tienes preguntas, sugerencias o quieres sumar tu comercio? Escríbenos y te responderemos lo antes posible.
					</p>
				</div>
				<div class="col-lg-4">
					<div class="glass-panel rounded-4 p-4 shadow-lg">
						<p class="mb-2 text-white-75">Atención</p>
						<p class="h5 fw-bold mb-2 text-white">Lunes a Viernes</p>
						<p class="mb-0 text-white-75">9:00 - 18:00</p>
						<hr class="border-white border-opacity-25 my-4" />
						<p class="mb-2 text-white-75">Email</p>
						<p class="mb-0 fw-semibold text-white">info@detubarrio.local</p>
					</div>
				</div>
			</div>
		</div>
	</section>

	<section class="py-5">
		<div class="container">
			<div class="row g-4 mb-5">
				<div class="col-md-6 col-xl-3">
					<div class="card h-100 border-0 shadow-sm rounded-4">
						<div class="card-body p-4">
							<i class="bi bi-envelope-paper fs-2 text-primary mb-3 d-inline-block"></i>
							<h2 class="h5 fw-bold">Email</h2>
							<p class="mb-1 fw-semibold">info@detubarrio.local</p>
							<p class="text-muted small mb-0">Consultas generales sobre la plataforma.</p>
						</div>
					</div>
				</div>
				<div class="col-md-6 col-xl-3">
					<div class="card h-100 border-0 shadow-sm rounded-4">
						<div class="card-body p-4">
							<i class="bi bi-telephone fs-2 text-primary mb-3 d-inline-block"></i>
							<h2 class="h5 fw-bold">Teléfono</h2>
							<p class="mb-1 fw-semibold">+34 91 XXX XXXX</p>
							<p class="text-muted small mb-0">Lunes a viernes, 9:00-18:00.</p>
						</div>
					</div>
				</div>
				<div class="col-md-6 col-xl-3">
					<div class="card h-100 border-0 shadow-sm rounded-4">
						<div class="card-body p-4">
							<i class="bi bi-geo-alt fs-2 text-primary mb-3 d-inline-block"></i>
							<h2 class="h5 fw-bold">Oficina</h2>
							<p class="mb-1 fw-semibold">DeTuBarrio S.L.</p>
							<p class="text-muted small mb-0">Calle Principal, 123, 28001 Madrid.</p>
						</div>
					</div>
				</div>
				<div class="col-md-6 col-xl-3">
					<div class="card h-100 border-0 shadow-sm rounded-4">
						<div class="card-body p-4">
							<i class="bi bi-shield-check fs-2 text-primary mb-3 d-inline-block"></i>
							<h2 class="h5 fw-bold">Seguridad</h2>
							<p class="mb-1 fw-semibold">Cifrado SSL</p>
							<p class="text-muted small mb-0">Protegemos tus datos y no compartimos información con terceros.</p>
						</div>
					</div>
				</div>
			</div>

			<div class="row g-4">
				<div class="col-lg-6">
					<div class="card border-0 shadow-sm rounded-4 h-100">
						<div class="card-body p-4 p-lg-5">
							<div class="d-flex align-items-center gap-2 mb-4">
								<span class="section-icon"><i class="bi bi-chat-dots-fill"></i></span>
								<h2 class="h3 fw-bold mb-0">Envíanos un mensaje</h2>
							</div>

							<form @submit.prevent="handleContactSubmit">
								<div class="row g-3">
									<div class="col-md-6">
										<label for="nombre" class="form-label">Nombre</label>
										<input id="nombre" v-model="contactForm.nombre" type="text" class="form-control form-control-lg" required @input="resetContactResponse" />
									</div>
									<div class="col-md-6">
										<label for="email" class="form-label">Email</label>
										<input id="email" v-model="contactForm.email" type="email" class="form-control form-control-lg" required @input="resetContactResponse" />
									</div>
									<div class="col-12">
										<label for="asunto" class="form-label">Asunto</label>
										<input id="asunto" v-model="contactForm.asunto" type="text" class="form-control form-control-lg" required @input="resetContactResponse" />
									</div>
									<div class="col-12">
										<label for="tipo" class="form-label">Tipo de consulta</label>
										<select id="tipo" v-model="contactForm.tipo" class="form-select form-select-lg" required @change="resetContactResponse">
											<option value="">Selecciona una opción...</option>
											<option value="soporte">Soporte técnico</option>
											<option value="sugerencia">Sugerencia / mejora</option>
											<option value="reporte">Reporte de problema</option>
											<option value="otro">Otro</option>
										</select>
									</div>
									<div class="col-12">
										<label for="mensaje" class="form-label">Mensaje</label>
										<textarea
											id="mensaje"
											v-model="contactForm.mensaje"
											class="form-control"
											rows="5"
											placeholder="Cuéntanos qué te trae por aquí..."
											required
											@input="resetContactResponse"
										></textarea>
									</div>
								</div>

								<button type="submit" class="btn btn-primary btn-lg mt-4 px-4" :disabled="isContactSubmitting">
									{{ isContactSubmitting ? 'Enviando...' : 'Enviar mensaje' }}
								</button>
							</form>

							<div v-if="contactError" class="alert alert-danger border-0 shadow-sm mt-4 mb-0" role="alert">
								{{ contactError }}
							</div>
							<div v-if="contactResponse" class="alert alert-success border-0 shadow-sm mt-4 mb-0" role="alert">
								{{ contactResponse }}
							</div>
						</div>
					</div>
				</div>

				<div class="col-lg-6">
					<div class="card border-0 shadow-sm rounded-4 h-100">
						<div class="card-body p-4 p-lg-5">
							<div class="d-flex align-items-center gap-2 mb-4">
								<span class="section-icon"><i class="bi bi-shop-window"></i></span>
								<h2 class="h3 fw-bold mb-0">¿Eres un comercio?</h2>
							</div>

							<p class="text-muted mb-4">
								Si tienes un comercio local y quieres formar parte de DeTuBarrio, rellena el formulario y nos pondremos en contacto contigo para darte de alta.
							</p>

							<form @submit.prevent="handleCollaboratorSubmit">
								<div class="row g-3">
									<div class="col-md-6">
										<label for="nombreComercio" class="form-label">Nombre del comercio</label>
										<input id="nombreComercio" v-model="collaboratorForm.nombreComercio" type="text" class="form-control form-control-lg" required @input="resetCollaboratorResponse" />
									</div>
									<div class="col-md-6">
										<label for="nombreTitular" class="form-label">Nombre del titular</label>
										<input id="nombreTitular" v-model="collaboratorForm.nombreTitular" type="text" class="form-control form-control-lg" required @input="resetCollaboratorResponse" />
									</div>
									<div class="col-md-6">
										<label for="emailComercio" class="form-label">Email de contacto</label>
										<input id="emailComercio" v-model="collaboratorForm.emailComercio" type="email" class="form-control form-control-lg" required @input="resetCollaboratorResponse" />
									</div>
									<div class="col-md-6">
										<label for="telefonoComercio" class="form-label">Teléfono</label>
										<input id="telefonoComercio" v-model="collaboratorForm.telefonoComercio" type="tel" class="form-control form-control-lg" required @input="resetCollaboratorResponse" />
									</div>
									<div class="col-12">
										<label for="categoria" class="form-label">Categoría de tu negocio</label>
										<select id="categoria" v-model="collaboratorForm.categoria" class="form-select form-select-lg" required @change="resetCollaboratorResponse">
											<option value="">Selecciona una categoría...</option>
											<option value="restauracion">Restauración</option>
											<option value="panaderia">Panadería</option>
											<option value="ferreteria">Ferretería</option>
											<option value="moda">Moda</option>
											<option value="salud">Salud y Belleza</option>
											<option value="otro">Otro</option>
										</select>
									</div>
									<div class="col-12">
										<label for="descripcion" class="form-label">Descripción de tu negocio</label>
										<textarea
											id="descripcion"
											v-model="collaboratorForm.descripcion"
											class="form-control"
											rows="4"
											placeholder="Cuéntanos qué ofrece tu negocio..."
											@input="resetCollaboratorResponse"
										></textarea>
									</div>
									<div class="col-12">
										<div class="form-check">
											<input
												id="terminos"
												v-model="collaboratorForm.terminos"
												class="form-check-input"
												type="checkbox"
												required
												@change="resetCollaboratorResponse"
											/>
											<label class="form-check-label" for="terminos">
												Acepto los términos y condiciones y autorizo a DeTuBarrio a contactarme.
											</label>
										</div>
									</div>
								</div>

								<button type="submit" class="btn btn-success btn-lg mt-4 px-4" :disabled="isCollaboratorSubmitting">
									{{ isCollaboratorSubmitting ? 'Enviando...' : 'Solicitar colaboración' }}
								</button>
							</form>

							<div v-if="collaboratorError" class="alert alert-danger border-0 shadow-sm mt-4 mb-0" role="alert">
								{{ collaboratorError }}
							</div>
							<div v-if="collaboratorResponse" class="alert alert-success border-0 shadow-sm mt-4 mb-0" role="alert">
								{{ collaboratorResponse }}
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>
</template>

<style scoped>
.contact-hero {
	background: linear-gradient(135deg, #0b2447 0%, #163e6f 55%, #2f73e0 100%);
}

.glass-panel {
	background: rgba(255, 255, 255, 0.1);
	backdrop-filter: blur(16px);
	border: 1px solid rgba(255, 255, 255, 0.18);
}

.section-icon {
	width: 44px;
	height: 44px;
	display: inline-flex;
	align-items: center;
	justify-content: center;
	border-radius: 50%;
	background: rgba(58, 134, 255, 0.12);
	color: var(--db-secondary);
	font-size: 1.1rem;
}

.text-white-75 {
	color: rgba(255, 255, 255, 0.78);
}
</style>
