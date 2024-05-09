"use client"

import { usePathname } from 'next/navigation'
import RedExIcon from '@/public/icons/LogoRedEx'
import NavbarItem from './NavbarItem'
import Link from 'next/link'
import LogoPlane from '@/public/icons/LogoPlane'
import LogoSimu from '@/public/icons/LogoSimu'
import LogoHome from '@/public/icons/LogoHome'
import LogoGest from '@/public/icons/LogoGest'
import LogoStats from '@/public/icons/LogoStats'
import LogoConfig from '@/public/icons/LogoConfig'

const Navbar = () => {
	const navbarItems = [
		{ name: 'Inicio', route: '/', Icon: LogoHome},
		{ name: 'En vivo', route: '/enVivo', Icon: LogoPlane},
		{ name: 'Gestión', route: '/gestion', Icon: LogoGest},
		{ name: 'Simulación', route: '/simulacion', Icon: LogoSimu},
		{ name: 'Estadísticas', route: '/estadisticas', Icon: LogoStats},
		{ name: '', route: '/configuracion', Icon: LogoConfig},
	]
	const pathname = usePathname()

	const isActive = (route) => {
		if (route === '/') {
			return pathname === '/';  // Solo marca como activo si es exactamente '/'
		}
		return pathname.startsWith(route);  // Para todas las demás rutas, usa el método de inicio
	}
	

	return (
		<nav className="bg-[#55BBBB] px-6 py-2 flex justify-between">
			<Link href="/">
				<div className="flex items-center">
					<RedExIcon className="mr-4" />
					<span className="self-center text-2xl font-bold tracking-wider  text-white"></span>
				</div>
			</Link>

			<div className="max-w-screen-xl flex flex-wrap items-center justify-between">
				<div className="hidden w-full md:block md:w-auto" id="navbar-default">
					<ul className=" flex flex-row font-semibold rounded-lg text-md text-white">
						{navbarItems.map((item) => (
							<NavbarItem
								key={item.route}
								name={item.name}
								route={item.route}
								icon={item.Icon}
								isActive={isActive(item.route)}
							/>
						))}
					</ul>
				</div>
			</div>
		</nav>
	)
}

export default Navbar
